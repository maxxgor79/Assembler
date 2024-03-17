package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DefCommandCompiler implements CommandCompiler {

  protected static final String[] NAMES = {"def", "define"};

  private final NamespaceApi namespaceApi;

  private final CompilerApi compilerApi;

  public DefCommandCompiler(@NonNull NamespaceApi namespaceApi
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
          .getMessage(MessageList.IDENTIFIER_EXPECTED_FOUND), nextLexem.getType().getName());
    }
    String name = nextLexem.getValue();
    if (!iterator.hasNext()) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.VALUE_EXCEPTED));
    }
    final Expression expression = new Expression(nextLexem.getFd(), iterator, namespaceApi);
    nextLexem = iterator.next();
    final Expression.Result result = expression.evaluate(nextLexem);
    if (result.isUndefined()) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber()
          , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
    }
    if (expression.getLastLexem() != null) {
      nextLexem = expression.getLastLexem();
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber()
          , MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());

    }
    if (namespaceApi.containsVariable(name)) {
      throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber(), MessageList.
          getMessage(MessageList.VARIABLE_IS_ALREADY_DEFINED), name);
    }
    namespaceApi.addVariable(name, result.getValue());
    return new byte[0];
  }
}
