package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.lexem.LexemAnalyzer;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.SyntaxAnalyzer;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author Maxim Gorin
 */
public final class CompilerFactory {
    private CompilerFactory() {

    }

    public static CompilerApi create(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull File file, @NonNull InputStream is, @NonNull OutputStream os) throws IOException {

        LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(file, is, settingsApi.getPlatformEncoding()
                , settingsApi.getSourceEncoding());
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new RepeatableIteratorImpl(lexemAnalyzer.iterator()));
        Compiler compiler = new Compiler(namespaceApi, settingsApi, syntaxAnalyzer, os);
        compiler.setFile(file);
        return compiler;
    }
}
