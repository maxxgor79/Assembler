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
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;
import ru.assembler.core.util.TypeUtil;

import java.math.BigInteger;

@Slf4j
public class EquCommandCompiler implements CommandCompiler {

  protected static final String[] NAMES = {"equ"};

  private final NamespaceApi namespaceApi;

  private final SettingsApi settingsApi;

  private final CompilerApi compilerApi;

  public EquCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
      , @NonNull CompilerApi compilerApi) {
    this.namespaceApi = namespaceApi;
    this.settingsApi = settingsApi;
    this.compilerApi = compilerApi;
  }

  @Override
  public String[] names() {
    return NAMES;
  }

  @Override
  public byte[] compile(@NonNull LexemSequence lexemSequence) {
    final RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(
        lexemSequence.get().iterator());
    Lexem nextLexem;
    if (!iterator.hasNext() || !contains(NAMES, (nextLexem = iterator.next()).getValue())) {
      return null;
    }
    if (iterator.hasNext()) {
      nextLexem = iterator.next();
    } else {
      throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.ADDRESS_EXCEPTED));
    }
    final Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
    final Expression.Result result = expression.evaluate(nextLexem);
    if (result.isUndefined()) {
      throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
          , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
    }
    final BigInteger equAddress = result.getValue();
    if (!TypeUtil.isInRange(BigInteger.ZERO, settingsApi.getMaxAddress(), equAddress)) {
      throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), String.valueOf(result.getValue()));
    }
    final String labelName = namespaceApi.getLabel(namespaceApi.getCurrentCodeOffset());
    if (labelName == null) {
      throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.LABEL_DECLARATION_REQUIRED));
    }
    //transform absolute address to relative
    namespaceApi.putLabel(labelName, equAddress.subtract(namespaceApi.getAddress()));
    nextLexem = expression.getLastLexem();
    if (nextLexem != null) {
      throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
          .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
    }
    return new byte[0];
  }
}
