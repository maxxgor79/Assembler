package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.utils.ConvertUtils;
import ru.zxspectrum.disassembler.utils.NumberStyle;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/5/2024
 */
public abstract class Number extends Cell {
    @Getter
    @Setter
    private static int radix = 16;

    @Getter
    @Setter
    @NonNull
    private static NumberStyle style = NumberStyle.Classic;

    @Getter
    @Setter
    @NonNull
    protected BigInteger value;

    protected String generateValue() {
        switch (radix) {
            case 2:
                return toUpperOrLowerCase(ConvertUtils.toBinary(value, style));
            case 8:
                return ConvertUtils.toOctal(value, style);
            case 10:
                return ConvertUtils.toDecimal(value, style);
            case 16:
                return toUpperOrLowerCase(ConvertUtils.toHex(value, style));
            default:
                throw new UnsupportedOperationException();
        }
    }
}
