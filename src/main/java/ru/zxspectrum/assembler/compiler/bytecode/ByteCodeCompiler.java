package ru.zxspectrum.assembler.compiler.bytecode;

import ru.zxspectrum.assembler.error.ParserException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public class ByteCodeCompiler {
    private int size;

    private CommandPatternParser parser;

    public ByteCodeCompiler(String codePattern, ByteOrder byteOrder) {
        if (codePattern == null || codePattern.trim().isEmpty()) {
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

    public byte[] compile(BigInteger... values) {
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
