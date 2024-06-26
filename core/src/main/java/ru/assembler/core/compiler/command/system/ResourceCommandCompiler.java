package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.FileUtil;

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
        if (!iterator.hasNext() || !contains(names(), iterator.next().getValue())) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem == null) {
            throw new CompilerException(compilerApi.getFd(), compilerApi.getLineNumber(), Messages
                    .getMessage(Messages.FILE_PATH_EXCEPTED));
        }
        while (true) {
            if (nextLexem.getType() == LexemType.STRING) {
                final String path = nextLexem.getValue();
                try {
                    baos.write(loadResource(FileUtil.toAbsolutePath(compilerApi.getFd().getFile().getParentFile()
                            , path)));
                } catch (FileNotFoundException e) {
                    throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), Messages
                            .getMessage(Messages.FILE_NOT_FOUND), path);
                } catch (IOException e) {
                    throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), Messages
                            .getMessage(Messages.FILE_READ_ERROR), path);
                }
            } else {
                throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), Messages
                        .getMessage(Messages.UNEXPECTED_SYMBOL), nextLexem.getValue());
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
