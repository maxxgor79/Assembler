package ru.assembler.microsha.core.compiler;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.Compiler;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.SyntaxAnalyzer;
import ru.assembler.microsha.core.compiler.command.system.RkmCommandCompiler;
import ru.assembler.microsha.core.compiler.command.system.SetCommandCompiler;
import ru.assembler.microsha.core.compiler.command.system.WavCommandCompiler;

import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */

public class MicroshaCompiler extends Compiler {
    public MicroshaCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
        super(namespaceApi, settingsApi, syntaxAnalyzer, os);
        addCommands();
    }

    private void addCommand(CommandCompiler cc) {
        for (String name : cc.names()) {
            addCommand(name, cc);
        }
    }

    private void addCommands() {
        addCommand(new WavCommandCompiler(this));
        addCommand(new RkmCommandCompiler(this));
        addCommand(new SetCommandCompiler(namespaceApi, this));
    }
}
