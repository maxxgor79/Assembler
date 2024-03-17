package ru.assembler.core.error;

import ru.assembler.core.error.text.MessageFormatter;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.io.FileDescriptor;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class DividingByZeroException extends ArithmeticException {
    public DividingByZeroException(FileDescriptor fd, int lineNumber) {
        super(MessageFormatter.generateErrorText(fd, lineNumber, MessageList
                .getMessage(MessageList.DIVIDING_BY_ZERO)));
    }
}
