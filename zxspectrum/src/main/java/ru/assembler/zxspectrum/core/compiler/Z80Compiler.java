package ru.assembler.zxspectrum.core.compiler;

import lombok.NonNull;
import ru.assembler.core.compiler.Compiler;
import ru.assembler.zxspectrum.core.compiler.command.system.TapCommandCompiler;
import ru.assembler.zxspectrum.core.compiler.command.system.WavCommandCompiler;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.SyntaxAnalyzer;

import java.io.OutputStream;

public class Z80Compiler extends Compiler {
    public Z80Compiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
        super(namespaceApi, settingsApi, syntaxAnalyzer, os);
        addCommands();
    }

    private void addCommands() {
        addCommand(WavCommandCompiler.NAME, new WavCommandCompiler(this));
        addCommand(TapCommandCompiler.NAME, new TapCommandCompiler(this));
    }
}
