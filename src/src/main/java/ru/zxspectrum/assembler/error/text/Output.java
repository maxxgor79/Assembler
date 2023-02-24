package ru.zxspectrum.assembler.error.text;

import ru.zxspectrum.assembler.error.AssemblerException;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public final class Output {
    private static int warningCount;

    private static int maxAdmirableWarningCount = Integer.MAX_VALUE;

    private Output() {

    }

    public static void println(String s) {
        System.out.println(s);
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

    public static int getWarningCount() {
        return warningCount;
    }

    public static int getMaxAdmirableWarningCount() {
        return maxAdmirableWarningCount;
    }

    public void setMaxAdmirableWarningCount(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value is negative");
        }
        maxAdmirableWarningCount = value;
    }
}
