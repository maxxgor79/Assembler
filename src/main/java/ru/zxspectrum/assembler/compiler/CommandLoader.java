package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static ru.zxspectrum.assembler.error.text.MessageList.INVALID_TABLE_FORMAT;

/**
 * @Author Maxim Gorin
 */
public abstract class CommandLoader<E> {
    protected E load(E value, InputStream is, Charset encoding) throws IOException {
        Scanner scanner = new Scanner(is, encoding);
        scanner.useDelimiter("[\t\r\n]");
        int lineNumber = 1;
        String code = null;
        String command = null;
        try {
            while (scanner.hasNextLine()) {
                code = scanner.next().trim();
                command = scanner.next().trim();
                parse(value, lineNumber, code, command);
                lineNumber++;
            }
        } catch(NoSuchElementException e) {
            throw new CompilerException(null, lineNumber, MessageList.getMessage(INVALID_TABLE_FORMAT), code + "\t" + command);
        }
        return value;
    }

    protected abstract void parse(E value, int lineNumber, String code, String command);

    public abstract E load(InputStream is, Charset encoding) throws IOException;

    public E load(InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }
}
