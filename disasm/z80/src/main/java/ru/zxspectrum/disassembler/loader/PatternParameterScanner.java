package ru.zxspectrum.disassembler.loader;

import lombok.NonNull;

import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.Scanner;


/**
 * @author Maxim Gorin
 * Date: 26.02.2023
 */
public class PatternParameterScanner {
    private Iterator<MatchResult> iter;

    public PatternParameterScanner(@NonNull String pattern) {
        Scanner scanner = new Scanner(pattern);
        this.iter = scanner.findAll("[$][nde]+").iterator();
    }

    public boolean hasNextVariable() {
        return iter.hasNext();
    }

    public String nextVariable() {
        return iter.next().group();
    }
}