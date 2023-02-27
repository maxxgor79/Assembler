package ru.zxspectrum.assembler.compiler;

import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.Scanner;


/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class PatternParameterScanner {
    private Iterator<MatchResult> iter;

    public PatternParameterScanner(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        Scanner scanner = new Scanner(pattern);
        this.iter = scanner.findAll("[$][aond]+").iterator();
    }

    public boolean hasNextVariable() {
        return iter.hasNext();
    }

    public String nextVariable() {
        return iter.next().group();
    }
}