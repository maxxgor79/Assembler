package ru.assembler.microsha.core.compiler;

import lombok.NonNull;
import ru.assembler.core.compiler.Compiler;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.SyntaxAnalyzer;

import java.io.OutputStream;

public class MicroshaCompiler extends Compiler {
    public MicroshaCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
        super(namespaceApi, settingsApi, syntaxAnalyzer, os);
        addCommands();
    }

    private void addCommands() {
    }
}
