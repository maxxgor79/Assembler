package ru.assembler.core.error.text;

import lombok.Getter;
import ru.assembler.core.error.AssemblerException;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public final class Output {
    @Getter
    private static int warningCount;

    @Getter
    private static int maxAdmirableWarningCount = Integer.MAX_VALUE;

    private Output() {

    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void formatPrintln(String mask, Object... args) {
        println(String.format(mask, args));
    }

    public static void throwWarning(File file, int lineNumber, String message, String... args) {
        String text = MessageFormatter.generateWarningText(file, lineNumber, message, args);
        System.out.println(text);
        warningCount++;
        if (warningCount >= maxAdmirableWarningCount) {
            throw new AssemblerException(text);
        }
    }

    public void setMaxAdmirableWarningCount(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value is negative");
        }
        maxAdmirableWarningCount = value;
    }
}
