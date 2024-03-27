package ru.zxspectrum.disassembler.decode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.concurrent.DecoderExecutor;
import ru.zxspectrum.disassembler.error.DecoderException;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.ByteCodeInputStream;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.lang.tree.Navigator;
import ru.zxspectrum.disassembler.lang.tree.Tree;
import ru.zxspectrum.disassembler.render.Canvas;
import ru.zxspectrum.disassembler.render.RowFactory;
import ru.zxspectrum.disassembler.render.command.Command;
import ru.zxspectrum.disassembler.render.command.CommandFactory;
import ru.zxspectrum.disassembler.render.command.Instruction;
import ru.zxspectrum.disassembler.render.command.Variable;
import ru.zxspectrum.disassembler.utils.ObjectUtils;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author maxim
 * Date: 12/24/2023
 */
@Slf4j
public class Decoder implements Runnable {
    private final DecoderExecutor executor;

    private final Tree<CommandFactory> commandTree;

    @Setter
    @Getter
    @NonNull
    private DecoderStrategy strategy = DecoderStrategy.Sequentially;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private BigInteger firstAddress;

    @Setter
    @Getter
    @NonNull
    private ByteCodeInputStream input;

    @Setter
    @Getter
    @NonNull
    private Canvas output;

    private boolean finish;

    final List<Variable> variables = new LinkedList<>();

    final List<ByteCodeUnit> cmdCodes = new LinkedList<>();

    public Decoder(@NonNull final DecoderExecutor executor, @NonNull final Tree commandTree
            , @NonNull final BigInteger firstAddress, @NonNull final ByteCodeInputStream data) {
        this.executor = executor;
        this.commandTree = commandTree;
        setFirstAddress(firstAddress);
        setInput(data);
    }

    @Override
    public void run() {
        try {
            finish = false;
            while (!finish) {
                final BigInteger address = getCurrentAddress();
                if (output.contains(address)) {
                    break;
                }
                int b = input.read();
                if (b == -1) {
                    break;
                }
                final Navigator<CommandFactory> navigator = commandTree.getNavigator();
                if (navigator.contains(ByteCodeUnit.valueOf(b))) {
                    input.pushback();
                    fetchCommand(address, navigator);
                } else {
                    throw new DecoderException(input.getFile(), input.getPc(), Messages.getMessage(Messages.UNKNOWN_COMMAND)
                            , b);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DecoderException(e);
        }
    }

    public BigInteger getCurrentAddress() {
        return firstAddress.add(BigInteger.valueOf(input.getPc()));
    }

    public BigInteger getLastAddress() {
        return firstAddress.add(BigInteger.valueOf(input.size() - 1));
    }

    private void fetchCommand(final BigInteger address, Navigator<CommandFactory> navigator) throws IOException {
        variables.clear();
        cmdCodes.clear();
        while (!finish) {
            int b = input.read();
            if (b == -1) {
                throw new EOFException();
            }
            ByteCodeUnit unit = ByteCodeUnit.valueOf(b);
            cmdCodes.add(unit);
            if (navigator.next(unit)) {
                if (navigator.isTerminal()) {
                    decode(address, navigator, variables);
                    break;
                }
            } else {
                unit = getPattern(navigator);
                if (unit != null) {
                    input.pushback();
                    switch (Type.getByPattern(unit.getValue())) {
                        case Int8 -> variables.add(new Variable(input.readByte(), Type.Int8));
                        case UInt8 -> variables.add(new Variable(input.read(), Type.UInt8));
                        case Int16 -> variables.add(new Variable(input.readShort(), Type.Int16));
                        case UInt16 -> variables.add(new Variable(input.readUnsignedShort(), Type.UInt16));
                        case Int32 -> variables.add(new Variable(input.readInt(), Type.Int32));
                        case UInt32 -> variables.add(new Variable(input.readUnsignedInt(), Type.UInt32));
                    }
                    if (navigator.next(unit)) {
                        if (navigator.isTerminal()) {
                            decode(address, navigator, variables);
                            break;
                        }
                    } else {
                        throw new DecoderException("Something wrong: " + unit);
                    }
                } else {
                    throw new DecoderException(input.getFile(), input.getPc(), Messages.getMessage(Messages.UNKNOWN_COMMAND)
                            , Arrays.toString(cmdCodes.toArray(new ByteCodeUnit[cmdCodes.size()])));
                }
            }
        }
    }

    private ByteCodeUnit getPattern(Navigator<CommandFactory> navigator) {
        for (Type type : Type.values()) {
            for (String pattern : type.getPatterns()) {
                ByteCodeUnit unit = ByteCodeUnit.valueOf(pattern);
                if (navigator.contains(unit)) {
                    return unit;
                }
            }
        }
        return null;
    }

    private void decode(BigInteger address, Navigator<CommandFactory> navigator, List<Variable> variables) {
        final Instruction instruction = navigator.getContent().create();
        instruction.setVariables(variables);
        switch (strategy) {
            case Sequentially -> decodeLinear(address, instruction);
            case Branching -> decodeBranching(address, instruction);
        }
    }

    private void decodeBranching(BigInteger address, Instruction instruction) {
        if (output != null) {
            output.put(address, RowFactory.createRow(instruction));
        } else {
            log.info("output is null");
        }
        switch (instruction.getBehavior()) {
            case Jump -> {
                jump(address, instruction);
            }
            case Skip -> {
                //do nothing
            }
            case Stop -> {
                finish = true;
            }
            case ConditionalJump -> {
                conditionalJump(address, instruction);
            }
        }
    }

    private void decodeLinear(final BigInteger address, final Command command) {
        if (output != null) {
            output.put(address, RowFactory.createRow(command));
        } else {
            log.info("output is null");
        }
    }

    private void jump(final BigInteger address, final Instruction instruction) {
        if (instruction.getVariableCount() == 0) {
            log.info("command has no address to jump");
            return;
        }
        final BigInteger jumpAddress = getAddressToJump(address, instruction);
        if (!ObjectUtils.isInRange(getFirstAddress(), getLastAddress(), jumpAddress) ||
                output.contains(jumpAddress)) {
            finish = true;
            return;
        }
        input.moveTo(jumpAddress.subtract(getCurrentAddress()).intValue());
    }

    private void conditionalJump(final BigInteger address, final Instruction instruction) {
        if (instruction.getVariableCount() == 0) {
            log.info("command has no address to jump");
            return;
        }
        final BigInteger jumpAddress = getAddressToJump(address, instruction);
        if (ObjectUtils.isInRange(getFirstAddress(), getLastAddress(), jumpAddress) &&
                !output.contains(jumpAddress)) {
            final Decoder newDecoder = clone();
            newDecoder.getInput().jump(jumpAddress.intValue());
            executor.execute(newDecoder);
        }
    }

    private BigInteger getAddressToJump(final BigInteger address, final Instruction instruction) {
        int index = instruction.getUnits().indexOfPattern(instruction.getJumpAddressPattern());
        if (index == -1) {
            throw new IllegalArgumentException("command does not contain pattern: "
                    + instruction.getJumpAddressPattern());
        }
        final Type addressType = Type.getByPattern(instruction.getJumpAddressPattern());
        if (addressType == null) {
            throw new IllegalArgumentException("address type is unknown");
        }
        final Variable var = instruction.getVariable(index);
        if (Type.isSigned(addressType)) {
            //offset
            BigInteger offset = BigInteger.valueOf(instruction.getByteCodeSize(index)).add(var.getValue());
            var.setValue(address.add(offset));
        }
        return var.getValue();
    }

    @Override
    public Decoder clone() {
        Decoder newDecoder = new Decoder(executor, commandTree, firstAddress, input.clone());
        newDecoder.setStrategy(DecoderStrategy.Sequentially);
        newDecoder.setOutput(output);
        return newDecoder;
    }
}
