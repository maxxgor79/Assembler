package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.util.*;


/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class PatternVariableScanner {
    private String pattern;

    private int curIndex;
    public PatternVariableScanner(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        this.pattern = pattern;
    }

    public boolean hasNextVariable() {
        return pattern.indexOf('$', curIndex) >= 0;
    }

    public String nextVariable() {
        int nextIndex = pattern.indexOf('$', curIndex);
        if (nextIndex == -1) {
            return null;
        }
        if (++nextIndex >= pattern.length()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (;nextIndex < pattern.length(); nextIndex++) {
            int ch = pattern.charAt(nextIndex);
            if (TypeUtil.isAddressPatternSymbol(ch) || TypeUtil.isAddressOffsetPatternSymbol(ch) ||
            TypeUtil.isNumberPattenSymbol(ch) || TypeUtil.isOffsetPatternSymbol(ch)) {
                sb.append((char)ch);
            } else {
                break;
            }
        }
        curIndex = nextIndex;
        return sb.toString();
    }
}
