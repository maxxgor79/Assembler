package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author maxim
 * Date: 1/5/2024
 */
public abstract class Number extends Cell {
    private static final Map<Integer, String> ZERO_MAP = new HashMap<>();

    static {
        ZERO_MAP.put(0, "");
        ZERO_MAP.put(1, "0");
        ZERO_MAP.put(2, "");
        ZERO_MAP.put(3, "0");
        ZERO_MAP.put(4, "");
        ZERO_MAP.put(5, "000");
        ZERO_MAP.put(6, "00");
        ZERO_MAP.put(7, "0");
        ZERO_MAP.put(8, "");
    }

    @Getter
    private static int radix = 16;

    @Getter
    @Setter
    @NonNull
    private static HexStyle style = HexStyle.Classic;

    @Getter
    @Setter
    @NonNull
    protected BigInteger value;

    protected static String toHex(BigInteger value) {
        String s = (value.toString(radix)).toUpperCase();
        return ZERO_MAP.get(s.length()) + s;
    }

    protected static String toHexStyle(BigInteger value) {
        return switch (style) {
            case Classic -> toHex(value) + "h";
            case C -> "0x" + toHex(value);
            case Nix -> "$" + toHex(value);
            case Rare -> "#" + toHex(value);
        };
    }

    protected String generateValue() {
        switch (radix) {
            case 10:
                return value.toString(radix);
            case 16:
                return toHexStyle(value);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public enum HexStyle {
        C, Classic, Nix, Rare;
    }
}
