package ru.zxspectrum.assembler.compiler.command.noparameterized;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zxspectrum.assembler.Assembler;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.syntax.LexemSequence;

/**
 * @Author Maxim Gorin
 */
@Slf4j
public class NoParametersCommandCompiler implements CommandCompiler {
    protected byte[] commandCode;

    protected LexemSequence originalLexemSequence;

    protected CompilerApi compilerApi;

    public NoParametersCommandCompiler(CompilerApi compilerApi, String code, String command) {
        this(compilerApi, code, new LexemSequence(command));
    }

    public NoParametersCommandCompiler(@NonNull CompilerApi compilerApi, @NonNull String code
            , @NonNull LexemSequence lexemSequence) {
        this.compilerApi = compilerApi;
        if (code.trim().isEmpty()) {
            throw new IllegalArgumentException("code is null or empty");
        }
        commandCode = toBytes(code);
        originalLexemSequence = lexemSequence;
    }

    @Override
    public byte[] compile(LexemSequence lexemSequence, boolean ignoreLabel) {
        if (originalLexemSequence.equals(lexemSequence)) {
            return commandCode;
        }
        return null;
    }

    private static byte[] toBytes(String code) {
        if (code == null || code.isEmpty()) {
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
