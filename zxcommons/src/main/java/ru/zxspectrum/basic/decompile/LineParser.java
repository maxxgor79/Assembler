package ru.zxspectrum.basic.decompile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.LexemType;
import ru.zxspectrum.basic.Operator;
import ru.zxspectrum.basic.Parser;
import ru.zxspectrum.basic.ParserException;
import ru.zxspectrum.basic.SymbolUtil;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class LineParser extends Parser {
    private final byte[] numberCodes = new byte[5];

    public LineParser() {

    }

    public LineParser(@NonNull InputStream is) {
        setInputStream(is);
    }

    public LineParser(@NonNull byte[] data) {
        setData(data);
    }

    @Override
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
            if (isEol(ch)) {
                lexem = getEol(ch);
            } else if (isDelimiter(ch)) {
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

    private Lexem getOperator(int ch) {
        Operator op = Operator.get(ch);
        return new Lexem(LexemType.Operator, op.getNativeName());
    }

    @Override
    protected Lexem getDelimiter(int ch) {
        StringBuilder sb = new StringBuilder();
        Operator op = Operator.get(ch);
        if (op != null) {
            sb.append(op.getNativeName());//<=, >=, <>
        } else {
            sb.append((char) ch);
        }
        return new Lexem(LexemType.Delimiter, sb.toString());
    }

    @Override
    protected Lexem getInteger(int ch) throws ParserException, IOException {
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
        lexem.setIntValue(bytes5ToInt(numberCodes));
        return lexem;
    }

    @Override
    protected boolean isEol(int ch) {
        return SymbolUtil.isEol(ch);
    }

    protected static boolean isDelimiter(int ch) {
        return "+-*/():\u00c7\u00c8\u00c9".indexOf(ch) != -1;
    }
}
