package ru.zxspectrum.assembler.compiler;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class PostCommandCompiler {
    private final CommandCompiler commandCompiler;

    @Getter
    private final BigInteger commandOffset;

    private final LexemSequence command;

    public PostCommandCompiler(@NonNull CommandCompiler commandCompiler, @NonNull BigInteger commandOffset
            , @NonNull LexemSequence command) {
        this.commandCompiler = commandCompiler;
        this.commandOffset = commandOffset;
        this.command = command;
    }

    public int compile(@NonNull RandomAccessFile randomAccessFile) throws IOException {
        byte[] generatedData = commandCompiler.compile(command);
        randomAccessFile.seek(commandOffset.longValue());
        randomAccessFile.write(generatedData);
        return generatedData.length;
    }
}
