package ru.zxspectrum.disassembler.lexem;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import ru.zxspectrum.disassembler.error.ParserException;
import ru.zxspectrum.disassembler.i18n.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author maxim
 * Date: 12/25/2023
 */
public class LexemParser {
    @Getter
    private int line;
    private PushbackInputStream pis;

    public void setInputStream(@NonNull InputStream is) {
        pis = new PushbackInputStream(is);
    }

    public Lexemes parse() throws IOException {
        List<Lexem> lexemList = new LinkedList<>();
        int ch;
        while ((ch = pis.read()) != -1) {
            if (ch == '\n') {
                line++;
            } else if (Character.isWhitespace(ch)) {
                //skip
            } else if (ch == '$') {
                lexemList.add(getVariableOrHexadecimal(ch));
            } else if (Character.isDigit(ch)) {
                lexemList.add(getNumber(ch));
            } else if (Character.isLetter(ch) || ch == '_') {
                lexemList.add(getIdentifier(ch));
            } else if (isDelimiter(ch)) {
                lexemList.add(getDelimiter(ch));
            } else if (ch == '\'') {
                lexemList.add(getChar(ch));
            } else if (ch == '"') {
                lexemList.add(getString(ch));
            } else {
                throw new ParserException(line, Messages.getMessage(Messages.UNKNOWN_SYMBOL)
                        , String.valueOf((char) ch));
            }
        }
        return new Lexemes(lexemList);
    }

    protected Lexem getVariableOrHexadecimal(int ch) throws IOException {
        ch = pis.read();
        if (Character.isDigit(ch)) {
            pis.unread(ch);
            return new Lexem(LexemType.Number, getNumber(LexemParser::isHexadecimal));
        } else {
            final Lexem identifier = getIdentifier(ch);
            return new Lexem(LexemType.Variable, identifier.getValue());
        }
    }

    protected Lexem getChar(int ch) throws IOException {
        final StringBuilder sb = new StringBuilder();
        boolean closedCharacter = false;
        while ((ch = pis.read()) != -1) {
            if (ch == '\'') {
                closedCharacter = true;
                break;
            }
            if (ch == '\n') {
                break;
            }
            sb.append((char) ch);
        }
        if (!closedCharacter) {
            throw new ParserException(line, Messages.getMessage(Messages.CHARACTER_NOT_CLOSED));
        }
        String text = StringEscapeUtils.unescapeJava(sb.toString());
        if (text.length() > 1) {
            throw new ParserException(line, Messages.getMessage(Messages.CHARACTER_TOO_LONG));
        }
        return new Lexem(LexemType.Symbol, text);
    }

    protected Lexem getString(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean closedString = false;
        while ((ch = pis.read()) != -1) {
            if (ch == '"') {
                closedString = true;
                break;
            }
            if (ch == '\n') {
                break;
            }
            sb.append((char) ch);
        }
        if (!closedString) {
            throw new ParserException(line, Messages.getMessage(Messages.STRING_NOT_CLOSED));
        }
        String text = StringEscapeUtils.unescapeJava(sb.toString());
        return new Lexem(LexemType.String, text);
    }

    private Lexem getDelimiter(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        switch (ch) {
            case '>':
                ch = pis.read();
                if (">=".indexOf(ch) != -1) {
                    sb.append((char) ch);
                } else {
                    pis.unread(ch);
                }
                break;
            case '<':
                ch = pis.read();
                if ("<=".indexOf(ch) != -1) {
                    sb.append((char) ch);
                } else {
                    pis.unread(ch);
                }
                break;
            case '=':
            case '!':
                ch = pis.read();
                if (ch == '=') {
                    sb.append((char) ch);
                } else {
                    pis.unread(ch);
                }
                break;
            case '|':
                ch = pis.read();
                if (ch == '|') {
                    sb.append((char) ch);
                } else {
                    pis.unread(ch);
                }
                break;
            case '&':
                ch = pis.read();
                if (ch == '&') {
                    sb.append((char) ch);
                } else {
                    pis.unread(ch);
                }
                break;
        }
        return new Lexem(LexemType.Delimiter, sb.toString());
    }

    protected Lexem getIdentifier(int ch) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while ((ch = pis.read()) != -1) {
            if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
                sb.append((char) ch);
            } else if (ch == '\'') {
                sb.append((char) ch);
                break;
            } else {
                pis.unread(ch);
                break;
            }
        }
        final String text = sb.toString();
        int lastCh = text.charAt(text.length() - 1);
        if ("hH".indexOf(lastCh) != -1) {
            final String number = StringUtils.chop(text);
            if (isNumberValid(number, LexemParser::isHexadecimal)) {
                return new Lexem(LexemType.Number, text);
            }
        }
        return new Lexem(LexemType.Identifier, text);
    }

    protected Lexem getNumber(int ch) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String number;
        if (ch == '0') {
            sb.append((char) ch);
            ch = pis.read();
            if ("xX".indexOf(ch) != -1) {
                sb.append((char) ch);
                sb.append(getNumber(LexemParser::isHexadecimal));
                number = sb.toString();
            } else if ("bB".indexOf(ch) != -1) {
                sb.append((char) ch);
                sb.append(getNumber(LexemParser::isHexadecimal));
                number = sb.toString();
                if (!isNumberValid(number, LexemParser::isBinary)) {
                    throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT),
                            Messages.getMessage(Messages.BINARY));
                }
            } else {
                pis.unread(ch);
                if (isHexadecimal(ch)) {
                    sb.append(getNumber(LexemParser::isHexadecimal));
                    number = sb.toString();
                    if (!isNumberValid(number, LexemParser::isOctal)) {
                        throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                                , Messages.getMessage(Messages.OCTAL));
                    }
                } else {
                    number = sb.toString();
                }
            }
            return new Lexem(LexemType.Number, number);
        } else {
            sb.append((char) ch);
            while ((ch = pis.read()) != -1) {
                if (isHexadecimal(ch)) {
                    sb.append((char) ch);
                } else {
                    break;
                }
            }
            number = sb.toString();
            if ("hH".indexOf(ch) != -1) {
                if (!isNumberValid(number, LexemParser::isHexadecimal)) {
                    throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                            , Messages.getMessage(Messages.HEXADECIMAL));
                }
                sb.append((char) ch);
            } else if ("qQ".indexOf(ch) != -1) {
                if (!isNumberValid(number, LexemParser::isOctal)) {
                    throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                            , Messages.getMessage(Messages.OCTAL));
                }
                sb.append((char) ch);
            } else if ("bB".indexOf(ch) != -1) {
                if (!isNumberValid(number, LexemParser::isBinary)) {
                    throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                            , Messages.getMessage(Messages.BINARY));
                }
                sb.append((char) ch);
            } else {
                if (!isNumberValid(number, LexemParser::isDecimal)) {
                    throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                            , Messages.getMessage(Messages.DECIMAL));
                }
                pis.unread(ch);
            }
            return new Lexem(LexemType.Number, sb.toString());
        }
    }

    protected String getNumber(Function<Integer, Boolean> isRadix) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = pis.read()) != -1) {
            if (isRadix.apply(ch)) {
                sb.append((char) ch);
            } else {
                pis.unread(ch);
                break;
            }
        }
        String number = sb.toString();
        if (number.length() < 1) {
            throw new ParserException(line, Messages.getMessage(Messages.BAD_NUMBER_FORMAT)
                    , "Not null");
        }
        return number;
    }

    protected boolean isNumberValid(String number, Function<Integer, Boolean> isRadix) {
        if (number.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < number.length(); i++) {
            if (!isRadix.apply((int) number.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isDelimiter(int ch) {
        return "()+-/*,^%!<>=~&|[]".indexOf(ch) != -1;
    }

    protected static boolean isHexadecimal(int ch) {
        return "0123456789abcdefABCDEF".indexOf(ch) != -1;
    }

    protected static boolean isBinary(int ch) {
        return "01".indexOf(ch) != -1;
    }

    protected static boolean isOctal(int ch) {
        return "01234567".indexOf(ch) != -1;
    }

    protected static boolean isDecimal(int ch) {
        return "0123456789".indexOf(ch) != -1;
    }
}
