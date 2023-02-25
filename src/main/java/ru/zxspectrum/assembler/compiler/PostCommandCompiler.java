package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public class PostCommandCompiler {
    private CommandCompiler commandCompiler;

    private BigInteger commandOffset;

    private LexemSequence command;

    public PostCommandCompiler(CommandCompiler commandCompiler, BigInteger commandOffset, LexemSequence command) {
        if (commandCompiler == null) {
            throw new NullPointerException("commandCompiler");
        }
        this.commandCompiler = commandCompiler;
        if (commandOffset == null) {
            throw new NullPointerException("commandOffset");
        }
        this.commandOffset = commandOffset;
        if (command == null) {
            throw new NullPointerException("command");
        }
        this.command = command;
    }

    public int compile(RandomAccessFile randomAccessFile) throws IOException {
        if (randomAccessFile == null) {
            throw new NullPointerException("randomAccessFile");
        }
        byte[] generatedData = commandCompiler.compile(command, false);
        randomAccessFile.seek(commandOffset.longValue());
        randomAccessFile.write(generatedData);
        return generatedData.length;
    }

    public BigInteger getCommandOffset() {
        return commandOffset;
    }
}
