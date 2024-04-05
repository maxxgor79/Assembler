package ru.assembler.core.error;

import ru.assembler.core.error.text.MessageFormatter;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.FileDescriptor;

/**
 * @author Maxim Gorin
 */
public class DividingByZeroException extends ArithmeticException {
    public DividingByZeroException(FileDescriptor fd, int lineNumber) {
        super(MessageFormatter.generateErrorText(fd, lineNumber, Messages
                .getMessage(Messages.DIVIDING_BY_ZERO)));
    }
}
