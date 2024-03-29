package ru.zxspectrum.disassembler.enrich;

import lombok.NonNull;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.bytecode.Pattern;
import ru.zxspectrum.disassembler.render.Address;
import ru.zxspectrum.disassembler.render.Canvas;
import ru.zxspectrum.disassembler.render.Comment;
import ru.zxspectrum.disassembler.render.Label;
import ru.zxspectrum.disassembler.render.Row;
import ru.zxspectrum.disassembler.render.command.Instruction;
import ru.zxspectrum.disassembler.render.command.Variable;
import ru.zxspectrum.disassembler.sys.Environment;

import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 28.03.2024
 */
public class Enricher {
    public static void enrichLabels(@NonNull Environment env, @NonNull Canvas canvas) {
        canvas.walkThrough(entry -> {
            final Row row = entry.getValue();
            if (row.getCommand() instanceof Instruction) {
                enrichInstruction(env, row, (Instruction) row.getCommand(), canvas);
            }
        });
    }

    private static void enrichInstruction(@NonNull final Environment env, final Row row, final Instruction inst
            , final Canvas canvas) {
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

    private static void translateRelativeAddress(@NonNull final Environment env, final Address address, final int offset
            , final Variable var
            , final Canvas canvas) {
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

    public static void enrichComment(@NonNull Environment env, @NonNull Canvas canvas) {
        canvas.walkThrough(entry -> {
            final Row row = entry.getValue();
            Comment comment = row.getComment();
            if (comment == null) {
                comment = new Comment(env);
                row.setComment(comment);
            }
            enrichComment(env, row, comment, canvas);
        });
    }

    private static void enrichComment(Environment env, Row row, Comment comment, Canvas canvas) {
        comment.setAddress(row.getAddress().getValue());
        comment.setByteCode(row.getCommand().toByteCode());
    }
}
