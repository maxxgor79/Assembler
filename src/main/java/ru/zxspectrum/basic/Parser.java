package ru.zxspectrum.basic;

import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;

public abstract class Parser {
    protected static final String EOL = "eol";

    protected PushbackInputStream is;

    public Parser() {

    }

    public Parser(@NonNull InputStream is) {
        setInputStream(is);
    }

    public Parser(@NonNull byte[] data) {
        setData(data);
    }

    public void setData(@NonNull byte[] data) {
        setInputStream(new ByteArrayInputStream((data)));
    }

    public void setInputStream(@NonNull InputStream is) {
        this.is = new PushbackInputStream(is);
    }

    public abstract Lexem next() throws ParserException, IOException;

    public List<Lexem> parse() throws ParserException, IOException {
        Lexem lexem;
        LinkedList<Lexem> list = new LinkedList<>();
        while ((lexem = next()) != null) {
            list.add(lexem);
        }
        if (!list.isEmpty() && list.getLast().getType() != LexemType.Eol) {
            list.add(new Lexem(LexemType.Eol, EOL));
        }
        return list;
    }

    protected Lexem getIdentifier(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while ((ch = is.read()) != -1) {
            if (isEol(ch)) {
                is.unread(ch);
                break;
            } else if (SymbolUtil.isAlphabet(ch) || SymbolUtil.isDigit(ch)) {
                sb.append((char) ch);
            } else if (SymbolUtil.isDollar(ch)) {
                sb.append((char) ch);
                break;
            } else {
                is.unread(ch);
                break;
            }
        }
        return new Lexem(LexemType.Identifier, sb.toString());
    }

    protected Lexem getEol(int ch) throws IOException {
        return new Lexem(LexemType.Eol, EOL);
    }

    protected Lexem getString(int ch) throws ParserException, IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            ch = is.read();
            if (ch == -1 || isEol(ch)) {
                throw new ParserException("'\"' is required");
            }
            if (SymbolUtil.isQuote(ch)) {
                break;
            }
            sb.append((char) ch);
        }
        return new StringLexem(LexemType.String, sb.toString());
    }

    protected Lexem getSymbol(int ch) {
        Lexem lexem = new Lexem(LexemType.Symbol, String.valueOf((char) ch));
        lexem.setIntValue(ch);
        return lexem;
    }

    protected abstract Lexem getDelimiter(int ch) throws ParserException, IOException;

    protected abstract Lexem getInteger(int ch) throws ParserException, IOException;

    public static int bytes5ToInt(@NonNull byte[] numberCodes) {
        int lsb = numberCodes[2] & 0xff;
        int msb = numberCodes[3] & 0xff;
        return lsb | (msb << 8);
    }

    public static byte[] intToBytes5(int a) {
        byte[] numberCodes = new byte[5];
        numberCodes[2] = (byte) a;
        numberCodes[3] = (byte) (a >>> 8);
        return numberCodes;
    }

    protected abstract boolean isEol(int ch);
}
