package ru.zxspectrum.assembler.lexem;

import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.util.SymbolUtils;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public final class Checker {
    private Checker() {

    }

    public static boolean isBinaryNumber(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!SymbolUtils.isBinaryDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOctalNumber(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!SymbolUtils.isOctalDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHexadecimalNumber(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!SymbolUtils.isHexDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDecimalNumber(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!SymbolUtils.isDecDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiEncoding(String s) {
        return isValidEncoding(s, Encoding.ASCII);
    }

    public static boolean isValidEncoding(String s, Encoding encoding) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        if (encoding == null) {
            return false;
        }
        int size = Encoding.sizeof(encoding);
        BigInteger min = TypeUtil.getMin(size);
        BigInteger max = TypeUtil.getMax(size);
        for (int i = 0; i < s.length(); i++) {
            BigInteger ch = BigInteger.valueOf(s.charAt(i));
            if (min.compareTo(ch) == 1 || max.compareTo(ch) == -1) {
                return false;
            }
        }
        return true;
    }
}
