package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.ConversationException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.error.text.Output;
import ru.assembler.core.lang.Type;
import ru.assembler.core.lang.TypeConverter;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.IOUtils;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;
import ru.assembler.core.util.TypeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class DwCommandCompiler implements CommandCompiler {

  protected static final String[] NAMES = {"dw", "defw"};

  protected CompilerApi compilerApi;

  protected NamespaceApi namespaceApi;

  protected SettingsApi settingsApi;

  public DwCommandCompiler(@NonNull NamespaceApi namespaceApi
      , @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi) {
    this.compilerApi = compilerApi;
    this.namespaceApi = namespaceApi;
    this.settingsApi = settingsApi;
  }

  @Override
  public String[] names() {
    return NAMES;
  }

  @Override
  public byte[] compile(LexemSequence lexemSequence) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(
          lexemSequence.get().iterator());
      Lexem nextLexem;
      if (!iterator.hasNext() || !contains(names(), (nextLexem = iterator.next()).getValue())) {
        return null;
      }
      nextLexem = iterator.hasNext() ? iterator.next() : null;
      while (true) {
        if (nextLexem == null) {
          throw new CompilerException(compilerApi.getFile(), compilerApi.getLineNumber(),
              MessageList
                  .getMessage(MessageList.VALUE_EXCEPTED));
        }
        if (nextLexem.getType() == LexemType.CHAR || nextLexem.getType() == LexemType.DECIMAL ||
            nextLexem.getType() == LexemType.OCTAL
            || nextLexem.getType() == LexemType.HEXADECIMAL) {
          final Expression expression = new Expression(compilerApi.getFile(), iterator,
              namespaceApi);
          final Expression.Result result = expression.evaluate(nextLexem);
          if (result.isUndefined()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
                , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
          }
          if (expression.getLastLexem() != null) {
            nextLexem = expression.getLastLexem();
          }
          BigInteger value = result.getValue();
          try {
            final Type srcType = TypeUtil.typeOf(value);
            value = TypeConverter.convert(srcType, value, getDestType(),
                settingsApi.isStrictConversion());
          } catch (ConversationException e) {
            log.error(e.getMessage(), e);
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.getValue().toString());
          }
          if (!result.getValue().equals(value)) {
            Output.throwWarning(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                , value.toString());
          }
          IOUtils.writeWord(baos, value.byteValue(), settingsApi.getByteOrder());
          nextLexem = expression.getLastLexem();
        } else {
          throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
              .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        if (nextLexem == null) {
          break;
        }
        if (nextLexem.getType() == LexemType.COMMA) {
          nextLexem = iterator.hasNext() ? iterator.next() : null;
        } else {
          throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
              .getMessage(MessageList.EXPECTED_SYMBOL), ",");
        }
      }
      return baos.toByteArray();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new CompilerException(e.getMessage(), e);
    }
  }

  protected Type getDestType() {
    return Type.UInt16;
  }
}
