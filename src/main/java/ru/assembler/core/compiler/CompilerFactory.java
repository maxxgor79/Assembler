package ru.assembler.core.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
    private CompilerFactory() {

    }

    public static CompilerApi create(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull File file, @NonNull InputStream is, @NonNull OutputStream os) throws IOException {
        return create(Compiler.class, namespaceApi, settingsApi, file, is, os);
    }

    public static CompilerApi create(@NonNull Class<?> clazz, @NonNull NamespaceApi namespaceApi
            , @NonNull SettingsApi settingsApi, @NonNull File file, @NonNull InputStream is, @NonNull OutputStream os)
            throws IOException {
        LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(file, is, settingsApi.getPlatformEncoding()
                , settingsApi.getSourceEncoding());
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new RepeatableIteratorImpl(lexemAnalyzer.iterator()));
        try {
            Compiler compiler = createCompiler(clazz, namespaceApi, settingsApi, syntaxAnalyzer, os);
            compiler.setFile(file);
            return compiler;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                 | IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("Bad class: " + clazz.getName());
        }
    }

    private static Compiler createCompiler(Class<?> clazz, NamespaceApi namespaceApi, SettingsApi settingsApi
            , SyntaxAnalyzer syntaxAnalyzer, OutputStream os) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> cons = clazz.getConstructor(NamespaceApi.class, SettingsApi.class, SyntaxAnalyzer.class
                , OutputStream.class);
        final Compiler compiler = (Compiler) cons.newInstance(namespaceApi, settingsApi, syntaxAnalyzer, os);
        return compiler;
    }
}
