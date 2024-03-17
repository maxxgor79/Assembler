package ru.assembler.core.compiler;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.io.FileDescriptor;
import ru.assembler.core.lexem.LexemAnalyzer;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.SyntaxAnalyzer;
import ru.assembler.core.util.RepeatableIteratorImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Maxim Gorin
 */
@Slf4j
public final class CompilerFactory {
    private static Factory defaultFactory = (namespace, settings, analyzer, os) -> new Compiler(namespace, settings
            , analyzer, os);

    private CompilerFactory() {

    }

    public static CompilerApi create(@NonNull final Factory factory, @NonNull NamespaceApi namespaceApi
            , @NonNull SettingsApi settingsApi, @NonNull FileDescriptor fd, @NonNull InputStream is
            , @NonNull OutputStream os) {
        LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(fd, is, settingsApi.getPlatformEncoding()
                , settingsApi.getSourceEncoding());
        lexemAnalyzer.setTrimEof(false);
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new RepeatableIteratorImpl(lexemAnalyzer.iterator()));
        Compiler compiler = factory.newCompiler(namespaceApi, settingsApi, syntaxAnalyzer, os);
        compiler.setFd(fd);
        return compiler;
    }

    public static CompilerApi create(@NonNull NamespaceApi namespaceApi
            , @NonNull SettingsApi settingsApi, @NonNull FileDescriptor fd, @NonNull InputStream is
            , @NonNull OutputStream os) {
        return create(defaultFactory, namespaceApi, settingsApi, fd, is, os);
    }

    public interface Factory {
        Compiler newCompiler(NamespaceApi namespace, SettingsApi settings, SyntaxAnalyzer analyzer, OutputStream os);
    }
}
