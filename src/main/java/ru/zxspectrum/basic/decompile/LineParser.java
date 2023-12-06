package ru.zxspectrum.basic.decompile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.basic.Operator;
import ru.zxspectrum.basic.SymbolUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class LineParser {
    private final byte[] numberCodes = new byte[5];

    private PushbackInputStream is;

    public LineParser() {

    }

    public LineParser(@NonNull InputStream is) {
        setInputStream(is);
    }

    public LineParser(@NonNull byte[] data) {
        setData(data);
    }

    public void setData(@NonNull byte[] data) {
        setInputStream(new ByteArrayInputStream((data)));
    }

    public void setInputStream(@NonNull InputStream is) {
        this.is = new PushbackInputStream(is);
    }

    public Lexem next() throws ParserException, IOException {
        Lexem lexem;
        while (true) {
            final int ch = is.read();
            if (ch == -1) {
                return null;
            }
            if (SymbolUtil.isWhite(ch)) {
                continue;
            }
            if (SymbolUtil.isEol(ch)) {
                lexem = getEol(ch);
            } else if (SymbolUtil.isDelimiter(ch)) {
                lexem = getDelimiter(ch);
            } else if (SymbolUtil.isDigit(ch)) {
                lexem = getInteger(ch);
            } else if (SymbolUtil.isAlphabet(ch)) {
                lexem = getIdentifier(ch);
            } else if (SymbolUtil.isQuote(ch)) {
                lexem = getString(ch);
            } else if (SymbolUtil.isOperator(ch)) {
                lexem = getOperator(ch);
            } else lexem = getSymbol(ch);
            return lexem;
        }
    }

    private Lexem getSymbol(int ch) {
        return new Lexem(LexemType.Symbol, String.valueOf((char) ch));
    }

    private Lexem getOperator(int ch) {
        Operator op = Operator.get(ch);
        return new Lexem(LexemType.Operator, op.getNativeName());
    }

    private Lexem getString(int ch) throws ParserException, IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            ch = is.read();
            if (ch == -1 || SymbolUtil.isEol(ch)) {
                throw new ParserException("'\"' is required");
            }
            if (SymbolUtil.isQuote(ch)) {
                break;
            }
            sb.append((char) ch);
        }
        return new StringLexem(LexemType.String, sb.toString());
    }

    private Lexem getDelimiter(int ch) {
        StringBuilder sb = new StringBuilder();
        Operator op = Operator.get(ch);
        if (op != null) {
            sb.append(op.getNativeName());//<=, >=, <>
        } else {
            sb.append((char) ch);
        }
        return new Lexem(LexemType.Delimiter, sb.toString());
    }

    private Lexem getInteger(int ch) throws ParserException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (true) {
            ch = is.read();
            if (SymbolUtil.isDigit(ch)) {
                sb.append((char) ch);
            } else {
                break;
            }
        }
        boolean numberCodesError = false;
        if (ch == 0x0e) {
            for (int i = 0; i < 5; i++) {
                ch = is.read();
                if (ch == -1) {
                    numberCodesError = true;
                    break;
                }
                numberCodes[i] = (byte) ch;
            }
        } else {
            numberCodesError = true;
        }
        Lexem lexem = new Lexem(LexemType.Number, sb.toString());
        if (numberCodesError) {
            throw new ParserException("Number bytes are excepted (" + lexem.getValue() + "), but found " + ch);
        }
        lexem.setIntValue(convert(numberCodes));
        return lexem;
    }

    private Lexem getEol(int ch) {
        return new Lexem(LexemType.Eol, "eol");
    }

    public List<Lexem> parse() throws ParserException, IOException {
        List<Lexem> list = new LinkedList<>();
        Lexem lexem;
        while ((lexem = next()) != null) {
            list.add(lexem);
        }
        return list;
    }

    private Lexem getIdentifier(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while ((ch = is.read()) != -1) {
            if (SymbolUtil.isEol(ch)) {
                is.unread(ch);
                break;
            } else if (SymbolUtil.isAlphabet(ch) || SymbolUtil.isDigit(ch)) {
                sb.append((char) ch);
            } else {
                is.unread(ch);
                break;
            }
        }
        return new Lexem(LexemType.Identifier, sb.toString());
    }

    protected static int convert(byte[] numberCodes) {
        int lsb = numberCodes[2] & 0xff;
        int msb = numberCodes[3] & 0xff;
        return lsb | (msb << 8);
    }
}
