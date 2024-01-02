package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.error.AssemblerException;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.util.SymbolUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import static ru.zxspectrum.assembler.error.text.MessageList.INVALID_TABLE_FORMAT;

/**
 * @author Maxim Gorin
 */
@Slf4j
public abstract class CommandLoader<E> {
    protected E load(E value, InputStream is, Charset encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
        String line;
        int lineNumber = 1;
        String codePattern = null;
        String commandPattern = null;
        Set<String> cpuSet;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                codePattern = data[0].trim();
                commandPattern = data[1].trim();
                if (data.length > 2) {
                    cpuSet = parseCpuModels(data[2].trim());
                } else {
                    cpuSet = Collections.emptySet();
                }
                if (!isPatternParametersAreEqual(codePattern, commandPattern)) {
                    throw new CompilerException(null, lineNumber, MessageList.getMessage(MessageList
                            .VARIABLE_PATTERNS_ARE_NOT_EQUAL), codePattern + "\t" + commandPattern);
                }
                prepare(value, lineNumber, codePattern, commandPattern, cpuSet);
                lineNumber++;
            }
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw new AssemblerException(null, lineNumber, MessageList.getMessage(INVALID_TABLE_FORMAT)
                    , codePattern + "\t" + commandPattern);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new AssemblerException(String.format("[%d][%s][%s]", lineNumber, codePattern, commandPattern));
        }
        return value;
    }

    private static Set<String> parseCpuModels(String cpuModels) {
      return SymbolUtil.parseEnumeration(cpuModels, ",");
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

    protected abstract void prepare(E value, int lineNumber, String code, String command
            , Set<String> cpues);

    public abstract E load(InputStream is, Charset encoding) throws IOException;

    public E load(@NonNull InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }
}
