package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.LexemSequence;

import java.util.Iterator;

public class EndCommandCompiler implements CommandCompiler {

  protected static final String[] NAMES = {"end"};

  private final NamespaceApi namespaceApi;

  private final CompilerApi compilerApi;

  public EndCommandCompiler(@NonNull NamespaceApi namespaceApi
      , @NonNull CompilerApi compilerApi) {
    this.namespaceApi = namespaceApi;
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
    if (!iterator.hasNext() || !contains(names(), iterator.next().getValue())) {
      return null;
    }
    if (iterator.hasNext()) {
      nextLexem = iterator.next();
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), Messages
          .getMessage(Messages.UNEXPECTED_SYMBOL), nextLexem.getValue());
    }
    compilerApi.stop();
    return new byte[0];
  }
}
