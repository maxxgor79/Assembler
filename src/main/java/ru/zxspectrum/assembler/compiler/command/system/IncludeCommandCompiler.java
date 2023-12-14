package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * @Author Maxim Gorin
 */
public class IncludeCommandCompiler implements CommandCompiler {
    public static final String NAME = "include";

    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

    public IncludeCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence, boolean ignoreLabel) {
        Iterator<Lexem> iterator = lexemSequence.get().iterator();
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (NAME.compareToIgnoreCase((nextLexem = iterator.next()).getValue()) != 0)) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem == null) {
            throw new CompilerException(compilerApi.getFile(), compilerApi.getLineNumber(), MessageList
                    .getMessage(MessageList.FILE_PATH_EXCEPTED));
        }
        while (true) {
            if (nextLexem.getType() == LexemType.STRING) {
                String path = nextLexem.getValue();
                nextLexem = iterator.hasNext() ? iterator.next() : null;
                try {
                    compileFile(nextLexem, path, ignoreLabel);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.FILE_READ_ERROR), path);
                }
            } else {
                throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
            }
            if (nextLexem == null) {
                break;
            }
        }
        return new byte[0];
    }

    private void compileFile(Lexem nextLexem, String path, boolean ignoreLabel) throws IOException {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(compilerApi.getFile().getParentFile(), path);
        }
        if (!file.exists()) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.FILE_NOT_FOUND), file.getAbsolutePath());
        }
        if (namespaceApi.isCompiled(file)) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.CYCLIC_DEPENDENCIES_ERROR), file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            CompilerApi compiler = CompilerFactory.create(namespaceApi, settingsApi, file, fis
                    , compilerApi.getOutputStream());
            namespaceApi.addCompiled(file);
            compiler.compile();
            compilerApi.addCompiledLineCount(compiler.getCompiledLineCount());
            compilerApi.addCompiledSourceCount(compiler.getCompiledSourceCount());
        }
    }
}
