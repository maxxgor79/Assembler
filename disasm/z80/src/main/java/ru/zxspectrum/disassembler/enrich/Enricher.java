package ru.zxspectrum.disassembler.enrich;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.bytecode.Pattern;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.render.*;
import ru.zxspectrum.disassembler.render.command.Instruction;
import ru.zxspectrum.disassembler.render.command.Variable;
import ru.zxspectrum.disassembler.sys.Environment;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 28.03.2024
 */
@Slf4j
public class Enricher {
    public static void enrichLabels(@NonNull Environment env, @NonNull Canvas canvas) {
        canvas.walkThrough(entry -> {
            final Row row = entry.getValue();
            if (row.getCommand() instanceof Instruction) {
                enrichInstruction(env, row, (Instruction) row.getCommand(), canvas);
            }
        });
    }

    private static void enrichInstruction(@NonNull final Environment env, final Row row, final Instruction inst, final Canvas canvas) {
        for (int i = 0; i < inst.getVariableCount(); i++) {
            final ByteCodeUnit bcUnit = inst.getUnits().getPattern(i);
            final Pattern pattern = new Pattern(bcUnit);
            final Variable var = inst.getVariable(i);
            if (pattern.isAddressOffset()) {
                //relative address
                translateRelativeAddress(env, row.getAddress(), inst.getUnits().getOffsetInBytes(i), var, canvas);
            } else if (pattern.isNumber() && pattern.getDimension() == env.getAddressDimension()) {
                // absolute address
                translateAbsoluteAddress(env, var, canvas);
            }
        }
    }

    private static void translateAbsoluteAddress(@NonNull Environment env, final Variable var, final Canvas canvas) {
        final Row labelRow = canvas.get(var.getValue());
        if (labelRow != null) {
            String labelName = env.getLabel(var.getValue());
            if (labelName == null) {
                labelName = Label.generateLabelName(env.getAddressDimension());
                env.putLabel(var.getValue(), labelName);
                labelRow.setLabel(new Label(labelName));
            }
            var.setName(labelName);
        }
    }

    private static void translateRelativeAddress(@NonNull final Environment env, final Address address, final int offset, final Variable var, final Canvas canvas) {
        final BigInteger absAddress = address.getValue().add(BigInteger.valueOf(offset)).add(var.getValue());
        final Row labelRow = canvas.get(absAddress);
        if (labelRow != null) {
            String labelName = env.getLabel(absAddress);
            if (labelName == null) {
                labelName = Label.generateLabelName(env.getAddressDimension());
                env.putLabel(absAddress, labelName);
                labelRow.setLabel(new Label(labelName));
            }
            var.setName(labelName);
        }
    }

    public static void enrichComments(@NonNull Environment env, @NonNull Canvas canvas) {
        canvas.walkThrough(entry -> {
            final Row row = entry.getValue();
            final Instruction instruction = (row.getCommand() instanceof Instruction) ? (Instruction) row.getCommand() : null;
            if (instruction != null) {
                Comment comment = row.getComment();
                if (comment == null) {
                    comment = new Comment(env);
                    row.setComment(comment);
                }
                enrichComment(env, row, instruction, comment, canvas);
            }
        });
    }

    private static void enrichComment(Environment env, Row row, Instruction inst, Comment comment, Canvas canvas) {
        comment.setAddress(row.getAddress().getValue());
        comment.setByteCode(row.getCommand().toByteCode());
        inst = inst.clone();
        if (alternative(inst)) {
            try {
                comment.setText(inst.generate());
            } catch (RenderException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static boolean alternative(Instruction inst) {
        if (inst.getJumpAddressPattern() != null || inst.getVariableCount() == 0) {
            return false;
        }
        int modificationCount = 0;
        for (Variable var : inst.getVariables()) {
            String translated;
            switch (var.getType()) {
                case UInt8 -> {
                    translated = SymbolUtils.translate(var.getValue().intValue());
                    if (translated != null) {
                        var.setName(translated);
                        modificationCount++;
                    }
                }
                case UInt16 -> {

                }
            }
        }
        return modificationCount > 0;
    }
}
