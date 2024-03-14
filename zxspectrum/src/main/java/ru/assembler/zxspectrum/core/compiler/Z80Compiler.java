package ru.assembler.zxspectrum.core.compiler;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.Compiler;
import ru.assembler.zxspectrum.core.compiler.command.system.TapCommandCompiler;
import ru.assembler.zxspectrum.core.compiler.command.system.TzxCommandCompiler;
import ru.assembler.zxspectrum.core.compiler.command.system.WavCommandCompiler;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.SyntaxAnalyzer;
import ru.assembler.zxspectrum.core.compiler.command.system.Z80CommandCompiler;

import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */

public class Z80Compiler extends Compiler {

  public Z80Compiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
      , @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
    super(namespaceApi, settingsApi, syntaxAnalyzer, os);
    addCommands();
  }

  private void addCommand(final CommandCompiler cc) {
    for (String name : cc.names()) {
      addCommand(name, cc);
    }
  }

  private void addCommands() {
    addCommand(new Z80CommandCompiler(this));
    addCommand(new WavCommandCompiler(this));
    addCommand(new TapCommandCompiler(this));
    addCommand(new TzxCommandCompiler(this));
  }
}
