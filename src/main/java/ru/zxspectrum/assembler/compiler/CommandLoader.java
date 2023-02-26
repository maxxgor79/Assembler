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
        String codePattern = null;
        String commandPattern = null;
        try {
            while (scanner.hasNextLine()) {
                codePattern = scanner.next().trim();
                commandPattern = scanner.next().trim();
                if (!patternVariablesAreEqual(codePattern, commandPattern)) {
                    throw new CompilerException(null, lineNumber, MessageList.getMessage(MessageList
                            .VARIABLE_PATTERNS_ARE_NOT_EQUAL), codePattern + "\t" + commandPattern);
                }
                parse(value, lineNumber, codePattern, commandPattern);
                lineNumber++;
            }
        } catch(NoSuchElementException e) {
            throw new CompilerException(null, lineNumber, MessageList.getMessage(INVALID_TABLE_FORMAT)
                    , codePattern + "\t" + commandPattern);
        }
        return value;
    }

    private static boolean patternVariablesAreEqual(String codePattern, String commandPattern) {
        PatternVariableScanner scanner1 = new PatternVariableScanner(codePattern);
        PatternVariableScanner scanner2 = new PatternVariableScanner(commandPattern);
        while(true) {
            boolean hasNextVariable1 = scanner1.hasNextVariable();
            boolean hasNextVariable2 = scanner2.hasNextVariable();
            if (hasNextVariable1 && hasNextVariable2) {
                if (!scanner1.nextVariable().equals(scanner2.nextVariable()))  {
                    return false;
                }
            } else {
                if (!hasNextVariable1 && !hasNextVariable2) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    protected abstract void parse(E value, int lineNumber, String code, String command);

    public abstract E load(InputStream is, Charset encoding) throws IOException;

    public E load(InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }
}
