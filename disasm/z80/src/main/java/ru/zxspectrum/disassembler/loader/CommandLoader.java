package ru.zxspectrum.disassembler.loader;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.command.Behavior;
import ru.zxspectrum.disassembler.error.ParserException;
import ru.zxspectrum.disassembler.i18n.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Maxim Gorin
 */
@Slf4j
public abstract class CommandLoader<E> {
    protected E load(E value, InputStream is, Charset encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
        int lineNumber = 1;
        String codePattern = null;
        String commandPattern = null;
        Behavior behavior = null;
        String jumpAddressPattern = null;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                if (data.length < 2) {
                    throw new IOException("Bad format");
                }
                codePattern = getCodePattern(data);
                commandPattern = getCommandPattern(data);
                behavior = getBehaviorValue(data);
                jumpAddressPattern = getJumpAddressPattern(behavior, data);
                if (!isPatternParametersAreEqual(codePattern, commandPattern)) {
                    throw new ParserException(null, lineNumber, Messages.getMessage(Messages
                            .VARIABLE_PATTERNS_ARE_NOT_EQUAL), codePattern + "\t" + commandPattern);
                }
                prepare(value, lineNumber, codePattern, commandPattern, behavior, jumpAddressPattern);
                lineNumber++;
            }
        } catch (RuntimeException e) {
            log.debug("[" + lineNumber + "]: " + e.getMessage());
            throw new ParserException(e.getMessage(), e);
        }
        return value;
    }

    private static String getCodePattern(@NonNull String[] data) {
        if (data.length < 1) {
            throw new IllegalArgumentException("code pattern is required");
        }
        return data[0].trim();
    }

    private static String getCommandPattern(@NonNull String[] data) {
        if (data.length < 2) {
            throw new IllegalArgumentException("command pattern is required");
        }
        return data[1].trim();
    }

    private static Behavior getBehaviorValue(@NonNull String[] data) {
        if (data.length > 2) {
            return Behavior.getBehavior(data[2].trim());
        }
        return null;
    }

    private static String getJumpAddressPattern(Behavior behavior, @NonNull String[] data) {
        if (behavior == null) {
            return null;
        }
        if (Behavior.isJumpGroup(behavior) && (data.length > 3)) {
            String mask = data[3].trim();
            if (mask.startsWith("$")) {
                mask = mask.substring(1);
            } else {
                throw new IllegalArgumentException("bad pattern format: " + mask);
            }
            return mask;
        }
        return null;
    }

    private static boolean isPatternParametersAreEqual(String codePattern, String commandPattern) {
        PatternParameterScanner scanner1 = new PatternParameterScanner(codePattern);
        PatternParameterScanner scanner2 = new PatternParameterScanner(commandPattern);
        while (true) {
            boolean hasNextVariable1 = scanner1.hasNextVariable();
            boolean hasNextVariable2 = scanner2.hasNextVariable();
            if (hasNextVariable1 && hasNextVariable2) {
                if (!scanner1.nextVariable().equals(scanner2.nextVariable())) {
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

    protected abstract void prepare(E value, int lineNumber, String code, String command, Behavior behavior,
                                    String jumpAddressPattern);

    public abstract E load(InputStream is, Charset encoding) throws IOException;

    public E load(@NonNull InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }
}
