package ru.zxspectrum.assembler.compiler.bytecode;

import lombok.NonNull;
import ru.zxspectrum.assembler.error.ParserException;
import ru.zxspectrum.assembler.util.SymbolUtils;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public class CommandPatternParser implements Cloneable {
    private String pattern;

    private ByteOrder byteOrder;

    protected CommandPatternParser() {

    }

    public CommandPatternParser(@NonNull String pattern, ByteOrder byteOrder) {
        if (pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("pattern is null or empty");
        }
        this.pattern = pattern;
        if (byteOrder == null) {
            byteOrder = ByteOrder.LittleEndian;
        }
        this.byteOrder = byteOrder;
    }

    private int getNumber(PushbackInputStream pis, int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        ch = pis.read();
        if (!SymbolUtils.isHexDigit(ch)) {
            throw new NumberFormatException();
        }
        sb.append((char) ch);
        return Integer.parseInt(sb.toString(), 16);
    }

    private String getVariable(PushbackInputStream pis, int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        int firstCh = pis.read();
        if (TypeUtil.isPatternSymbol(firstCh)) {
            sb.append((char) firstCh);
            while (!SymbolUtils.isEOS(ch = pis.read())) {
                if (firstCh == ch && TypeUtil.isPatternSymbol(ch)) {
                    sb.append((char) ch);
                } else {
                    break;
                }
            }
        } else {
            throw new ParserException("Invalid variable name");
        }
        if (!SymbolUtils.isEOS(ch)) {
            pis.unread(ch);
        }
        return sb.toString();
    }

    public byte[] parse(@NonNull BigInteger... values) throws IOException {
        PushbackInputStream pis = new PushbackInputStream(new ByteArrayInputStream(pattern.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ch;
        int argIndex = 0;
        while ((ch = pis.read()) != -1) {
            if (SymbolUtils.isHexDigit(ch)) {
                baos.write(getNumber(pis, ch));
            } else {
                if (SymbolUtils.isDollar(ch)) {
                    String variable = getVariable(pis, ch);
                    BigInteger arg = BigInteger.ZERO;
                    if (argIndex < values.length) {
                        arg = values[argIndex++];
                    }
                    baos.write(TypeUtil.toBytes(arg, TypeUtil.toType(variable), byteOrder));
                } else {
                    throw new ParserException("Invalid pattern format '" + pattern + "'");
                }
            }
        }
        return baos.toByteArray();
    }

    public String getPattern() {
        return pattern;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public CommandPatternParser clone() {
        return new CommandPatternParser(getPattern(), getByteOrder());
    }
}
