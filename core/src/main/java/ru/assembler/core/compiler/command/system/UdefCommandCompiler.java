package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;

/**
 * @author Maxim Gorin
 */
public class UdefCommandCompiler implements CommandCompiler {

  protected static final String[] NAMES = {"udef", "undefine"};

  private final NamespaceApi namespaceApi;

  private final CompilerApi compilerApi;

  public UdefCommandCompiler(@NonNull NamespaceApi namespaceApi
      , @NonNull CompilerApi compilerApi) {
    this.namespaceApi = namespaceApi;
    this.compilerApi = compilerApi;
  }

  @Override
  public String[] names() {
    return NAMES;
  }

  @Override
  public byte[] compile(LexemSequence lexemSequence) {
    RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(
        lexemSequence.get().iterator());
    Lexem nextLexem;
    if (!iterator.hasNext() || !contains(names(), (nextLexem = iterator.next()).getValue())) {
      return null;
    }
    if (!iterator.hasNext()) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.IDENTIFIER_EXPECTED));
    }
    nextLexem = iterator.next();
    if (nextLexem.getType() != LexemType.IDENTIFIER) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.IDENTIFIER_EXPECTED_FOUND), nextLexem.getValue());
    }
    final String name = nextLexem.getValue();
    namespaceApi.removeVariable(name);
    nextLexem = iterator.next();
    if (nextLexem != null) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
    }
    return new byte[0];
  }
}
