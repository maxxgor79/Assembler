package ru.zxspectrum.assembler.compiler.command.noparameterized;

import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.syntax.LexemSequence;

/**
 * @Author Maxim Gorin
 */
public class NoParametersCommandCompiler implements CommandCompiler {
    protected byte[] commandCode;

    protected LexemSequence originalLexemSequence;

    protected CompilerApi compilerApi;

    public NoParametersCommandCompiler(CompilerApi compilerApi, String code, String command) {
        this(compilerApi, code, new LexemSequence(command));
    }

    public NoParametersCommandCompiler(CompilerApi compilerApi, String code, LexemSequence lexemSequence) {
        if (compilerApi == null) {
            throw new NullPointerException("compilerApi");
        }
        this.compilerApi = compilerApi;
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("code is null or empty");
        }
        if (lexemSequence == null) {
            throw new IllegalArgumentException("lexemSequence is null or empty");
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
        for (int j = 0, i = 0; j < bytesCount; j++, i += 2) {
            result[j] = (byte) Integer.parseInt(code.substring(i, i + 2), 16);
        }
        return result;
    }
}
