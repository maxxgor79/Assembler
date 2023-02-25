package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.NamespaceApi;
import ru.zxspectrum.assembler.lexem.LexemAnalyzer;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.SyntaxAnalyzer;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author Maxim Gorin
 */
public final class CompilerFactory {
    private CompilerFactory() {

    }

    public static CompilerApi create(NamespaceApi namespaceApi, SettingsApi settingsApi
            , File file, OutputStream os) throws IOException {
        if (namespaceApi == null) {
            throw new NullPointerException("namespaceApi");
        }
        if (settingsApi == null) {
            throw new NullPointerException("settingsApi");
        }
        if (file == null) {
            throw new NullPointerException("file");
        }
        if (os == null) {
            throw new NullPointerException("os");
        }
        FileInputStream fis = new FileInputStream(file);
        LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(file, fis, settingsApi.getPlatformEncoding()
                , settingsApi.getSourceEncoding());
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new RepeatableIteratorImpl(lexemAnalyzer.iterator()));
        Compiler compiler = new Compiler(namespaceApi, settingsApi, syntaxAnalyzer, fis, os);
        compiler.setFile(file);
        return compiler;
    }
}
