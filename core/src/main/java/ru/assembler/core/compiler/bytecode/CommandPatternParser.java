package ru.assembler.core.compiler.bytecode;

import lombok.NonNull;
import ru.assembler.core.error.ParserException;
import ru.assembler.core.util.SymbolUtil;
import ru.assembler.core.util.TypeUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public class CommandPatternParser implements Cloneable {
    protected String pattern;

    protected ByteOrder byteOrder;

    protected static final byte[] buf = new byte[256];

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
        final StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        ch = pis.read();
        if (!SymbolUtil.isHexDigit(ch)) {
            throw new NumberFormatException();
        }
        sb.append((char) ch);
        return Integer.parseInt(sb.toString(), 16);
    }

    private String getVariable(PushbackInputStream pis, int ch) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int firstCh = pis.read();
        if (TypeUtil.isPatternSymbol(firstCh)) {
            sb.append((char) firstCh);
            while (!SymbolUtil.isEOF(ch = pis.read())) {
                if (firstCh == ch && TypeUtil.isPatternSymbol(ch)) {
                    sb.append((char) ch);
                } else {
                    break;
                }
            }
        } else {
            throw new ParserException("Invalid variable name");
        }
        if (!SymbolUtil.isEOF(ch)) {
            pis.unread(ch);
        }
        return sb.toString();
    }

    List<Object> preTranslate() throws IOException {
        final PushbackInputStream pis = new PushbackInputStream(new ByteArrayInputStream(pattern.getBytes()));
        final List<Object> list = new ArrayList<>();
        int index = 0;
        int ch;
        while ((ch = pis.read()) != -1) {
            if (SymbolUtil.isHexDigit(ch)) {
                buf[index++] = (byte) getNumber(pis, ch);
            } else {
                if (SymbolUtil.isDollar(ch)) {
                    if (index > 0) {
                        list.add(Arrays.copyOf(buf, index));
                        index = 0;
                    }
                    list.add(TypeUtil.toType(getVariable(pis, ch)));
                } else {
                    throw new ParserException("Invalid pattern format '" + pattern + "'");
                }
            }
        }
        if (index > 0) {
            list.add(Arrays.copyOf(buf, index));
        }
        return list;
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
