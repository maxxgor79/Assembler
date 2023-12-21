package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.error.AssemblerException;
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
@Slf4j
public abstract class CommandLoader<E> {
    protected E load(E value, InputStream is, Charset encoding) throws IOException {
        Scanner scanner = new Scanner(is, encoding);
        scanner.useDelimiter("[\t\n]+");
        int lineNumber = 1;
        String codePattern = null;
        String commandPattern = null;
        try {
            while (scanner.hasNextLine()) {
                codePattern = scanner.next().trim();
                commandPattern = scanner.next().trim();
                if (!isPatternParametersAreEqual(codePattern, commandPattern)) {
                    throw new CompilerException(null, lineNumber, MessageList.getMessage(MessageList
                            .VARIABLE_PATTERNS_ARE_NOT_EQUAL), codePattern + "\t" + commandPattern);
                }
                prepare(value, lineNumber, codePattern, commandPattern);
                lineNumber++;
            }
        } catch(NoSuchElementException e) {
            throw new AssemblerException(null, lineNumber, MessageList.getMessage(INVALID_TABLE_FORMAT)
                    , codePattern + "\t" + commandPattern);
        }
        catch (RuntimeException e) {
            log.debug("" + lineNumber + ": " + e.getMessage());
            throw new AssemblerException(e.getMessage());
        }
        return value;
    }

    private static boolean isPatternParametersAreEqual(String codePattern, String commandPattern) {
        PatternParameterScanner scanner1 = new PatternParameterScanner(codePattern);
        PatternParameterScanner scanner2 = new PatternParameterScanner(commandPattern);
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

    protected abstract void prepare(E value, int lineNumber, String code, String command);

    public abstract E load(InputStream is, Charset encoding) throws IOException;

    public E load(@NonNull InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }
}
