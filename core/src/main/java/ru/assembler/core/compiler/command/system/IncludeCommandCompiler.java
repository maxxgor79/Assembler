package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.Output;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class IncludeCommandCompiler implements CommandCompiler {

    protected static final String[] NAMES = {"include"};

    private final NamespaceApi namespaceApi;

    private final SettingsApi settingsApi;

    private final CompilerApi compilerApi;

    public IncludeCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        Iterator<Lexem> iterator = lexemSequence.get().iterator();
        Lexem nextLexem;
        if (!iterator.hasNext() || !contains(names(), (nextLexem = iterator.next()).getValue())) {
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
                    if (!compilerApi.include(FileUtil.toAbsolutePath(compilerApi.getFd().getFile().getParentFile(), path))) {
                        Output.throwWarning(nextLexem.getFd(), nextLexem.getLineNumber(), Messages
                                .getMessage(Messages.FILE_IS_ALREADY_INCLUDED), path);
                    }
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
        return new byte[0];
    }
}
