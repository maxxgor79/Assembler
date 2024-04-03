package ru.zxspectrum.disassembler.render.system;

import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.render.command.Command;
import ru.zxspectrum.disassembler.utils.ConverterUtils;

import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 03.04.2024
 */
public class Db extends Command {
    protected static final String NAME = "db";

    protected byte[] values;

    public Db(final byte[] values) {
        this.values = values;
    }

    @Override
    public String generate() throws RenderException {
        final StringBuilder sb = new StringBuilder();
        sb.append(toUpperOrLowerCase(NAME)).append(" ");
        int i = 0;
        for (byte b : values) {
            switch (ru.zxspectrum.disassembler.render.Number.getRadix()) {
                case 2:
                    toUpperOrLowerCase(ConverterUtils.toBinary(BigInteger.valueOf(b)
                            , ru.zxspectrum.disassembler.render.Number.getStyle()));
                    break;
                case 8:
                    ConverterUtils.toOctal(BigInteger.valueOf(b)
                            , ru.zxspectrum.disassembler.render.Number.getStyle());
                    break;
                case 10:
                    ConverterUtils.toDecimal(BigInteger.valueOf(b)
                            , ru.zxspectrum.disassembler.render.Number.getStyle());
                    break;
                case 16:
                    toUpperOrLowerCase(ConverterUtils.toHex(BigInteger.valueOf(b)
                            , ru.zxspectrum.disassembler.render.Number.getStyle()));
                    break;
                default:
                    throw new NumberFormatException();
            }
            if (++i < values.length) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    @Override
    public byte[] toByteCode() {
        return values;
    }
}
