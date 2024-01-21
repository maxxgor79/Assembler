package ru.assembler.core.error;

import ru.assembler.core.error.text.MessageFormatter;
import ru.assembler.core.error.text.MessageList;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class DividingByZeroException extends ArithmeticException {
    public DividingByZeroException(File file, int lineNumber) {
        super(MessageFormatter.generateErrorText(file, lineNumber, MessageList
                .getMessage(MessageList.DIVIDING_BY_ZERO)));
    }
}
