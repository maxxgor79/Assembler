package ru.assembler.core.io;

import lombok.NonNull;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public final class ErrorOutput {
    private ErrorOutput() {

    }

    public static void println(String s) {
        System.err.println(s);
    }

    public static void print(final String s) {
        System.err.print(s);
    }

    public static void formatPrintln(@NonNull final String s, final Object... args) {
        println(String.format(s, args));
    }

    public static void formatPrint(@NonNull final String s, final Object... args) {
        print(String.format(s, args));
    }
}
