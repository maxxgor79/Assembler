package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.syntax.LexemSequence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class ResourceCommandCompiler implements CommandCompiler {
    protected static final String[] NAMES = {"resource", "defres"};

    protected CompilerApi compilerApi;

    public ResourceCommandCompiler(@NonNull final CompilerApi compilerApi) {
        this.compilerApi = compilerApi;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public byte[] compile(@NonNull final LexemSequence lexemSequence) {
        Lexem nextLexem;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Iterator<Lexem> iterator = lexemSequence.get().iterator();
        if (!iterator.hasNext() || !contains(names(), ((iterator.next()).getValue()))) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem == null) {
            throw new CompilerException(compilerApi.getFile(), compilerApi.getLineNumber(), MessageList
                    .getMessage(MessageList.FILE_PATH_EXCEPTED));
        }
        while (true) {
            if (nextLexem.getType() == LexemType.STRING) {
                final String path = nextLexem.getValue();
                try {
                    baos.write(loadResource(path));
                } catch (FileNotFoundException e) {
                    throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.FILE_NOT_FOUND), path);
                } catch (IOException e) {
                    throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.FILE_READ_ERROR), path);
                }
            } else {
                throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
            }
            nextLexem = iterator.hasNext() ? iterator.next() : null;
            if (nextLexem == null) {
                break;
            }
        }
        return baos.toByteArray();
    }

    protected byte[] loadResource(@NonNull String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return IOUtils.toByteArray(fis);
        }
    }
}
