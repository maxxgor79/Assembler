package ru.zxspectrum.disassembler.bytecode;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.error.ParserException;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author maxim
 * Date: 12/26/2023
 */
@Slf4j
public class CodePatternParser {
    @Setter
    @Getter
    @NonNull
    private String codePattern;

    private PushbackInputStream pis;

    public ByteCodeUnits parse() {
        try {
            List<ByteCodeUnit> units = new LinkedList<>();
            int ch;
            pis = new PushbackInputStream(new ByteArrayInputStream(codePattern.getBytes()));
            while ((ch = pis.read()) != -1) {
                if (SymbolUtils.isHexadecimalDigit(ch)) {
                    units.add(new ByteCodeUnit(ByteCodeType.Code, getCode(ch)));
                } else if (SymbolUtils.isDollar(ch)) {
                    units.add(new ByteCodeUnit(ByteCodeType.Pattern, getPattern(ch)));
                } else {
                    throw new ParserException("Unknown symbol '" + (char) ch + "'");
                }
            }
            return new ByteCodeUnits(units);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ParserException(e);
        }
    }

    private String getCode(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        ch = pis.read();
        if (SymbolUtils.isHexadecimalDigit(ch)) {
            sb.append((char) ch);
            return sb.toString();
        }
        throw new ParserException("Hex digit excepted");
    }

    private String getPattern(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        ch = pis.read();
        if (!SymbolUtils.isPatternLetter(ch)) {
            throw new ParserException("Pattern letter is expected, but found '" + (char) ch + "'");
        }
        sb.append((char) ch);
        int patternLetter = ch;
        while ((ch = pis.read()) != -1) {
            if (ch == patternLetter) {
                sb.append((char) ch);
            } else {
                pis.unread(ch);
                break;
            }
        }
        final String pattern = sb.toString();
        if (Type.getByPattern(pattern) == Type.Unknown) {
            throw new ParserException("Bad pattern format: " + pattern);
        }
        return pattern;
    }
}
