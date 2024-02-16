package ru.assembler.core.compiler.command.nonparameterized;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.syntax.LexemSequence;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class NonParametersCommandCompiler implements CommandCompiler {

  protected byte[] commandCode;

  protected LexemSequence originalLexemSequence;

  protected CompilerApi compilerApi;

  public NonParametersCommandCompiler(CompilerApi compilerApi, String code, String command) {
    this(compilerApi, code, new LexemSequence(command));
  }

  public NonParametersCommandCompiler(@NonNull CompilerApi compilerApi, @NonNull String code
      , @NonNull LexemSequence lexemSequence) {
    this.compilerApi = compilerApi;
    if (code.trim().isEmpty()) {
      throw new IllegalArgumentException("code is null or empty");
    }
    commandCode = toBytes(code);
    originalLexemSequence = lexemSequence;
  }

  @Override
  public String[] names() {
    return null;
  }

  @Override
  public byte[] compile(@NonNull LexemSequence lexemSequence) {
    if (originalLexemSequence.equals(lexemSequence)) {
      return commandCode;
    }
    return null;
  }

  private static byte[] toBytes(@NonNull String code) {
    if (code.isEmpty()) {
      return new byte[0];
    }
    int bytesCount = code.length() / 2;
    if (bytesCount == 0) {
      throw new IllegalArgumentException("Bad code size!");
    }
    byte[] result = new byte[bytesCount];
    try {
      for (int j = 0, i = 0; j < bytesCount; j++, i += 2) {
        result[j] = (byte) Integer.parseInt(code.substring(i, i + 2), 16);
      }
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    return result;
  }
}
