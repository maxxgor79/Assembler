package ru.zxspectrum.assembler.compiler.bytecode;

import lombok.NonNull;
import ru.zxspectrum.assembler.error.ParserException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public class ByteCodeCompiler {
    private int size;

    private CommandPatternParser parser;

    public ByteCodeCompiler(@NonNull String codePattern, ByteOrder byteOrder) {
        if (codePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("codePattern is null or empty");
        }
        parser = new CommandPatternParser(codePattern, byteOrder);
        size = calculateSize();
    }

    private int calculateSize() {
        try {
            byte[] data = parser.parse(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
            return data.length;
        } catch (IOException e) {
            return 0;
        }
    }

    public byte[] compile(@NonNull BigInteger... values) {
        try {
            return parser.parse(values);
        } catch (IOException e) {
            throw new ParserException("IO Error");
        }
    }

    public int getSize() {
        return size;
    }
}
