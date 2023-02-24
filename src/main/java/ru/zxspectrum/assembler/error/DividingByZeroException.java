package ru.zxspectrum.assembler.error;

import ru.zxspectrum.assembler.error.text.MessageFormatter;
import ru.zxspectrum.assembler.error.text.MessageList;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public class DividingByZeroException extends ArithmeticException {
    public DividingByZeroException(File file, int lineNumber) {
        super(MessageFormatter.generateErrorText(file, lineNumber, MessageList
                .getMessage(MessageList.DIVIDING_BY_ZERO)));
    }
}
