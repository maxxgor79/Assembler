package ru.zxspectrum.assembler.lexem;

import org.apache.commons.text.StringEscapeUtils;
import ru.zxspectrum.assembler.error.BadCharsetEncodingException;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.InvalidFormatNumberException;
import ru.zxspectrum.assembler.error.LexemException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.util.AnalyzerIterator;
import ru.zxspectrum.assembler.util.SymbolUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @Author Maxim Gorin
 */
public class LexemAnalyzer implements Iterable<Lexem> {
    private final static Encoding DEFAULT_ENCODING = Encoding.UTF_8;

    private final static Encoding DEFAULT_PLATFORM_ENCODING = Encoding.ASCII;


    private PushbackReader pbReader;

    private int lineNumber = 1;

    private File file;

    private LexemInternalIterator lexemInternalIterator;

    private Encoding encoding;

    private Encoding platformEncoding;

    private LexemAnalyzer() {

    }

    public LexemAnalyzer(File file, InputStream is) {
        this(file, is, DEFAULT_PLATFORM_ENCODING, DEFAULT_ENCODING);
    }

    public LexemAnalyzer(File file, InputStream is, Encoding platformEncoding) {
        this(file, is, platformEncoding, DEFAULT_ENCODING);
    }

    public LexemAnalyzer(File file, InputStream is, Encoding platformEncoding, Encoding encoding) {
        this.file = file;
        if (is == null) {
            throw new NullPointerException("is");
        }
        if (platformEncoding == null) {
            platformEncoding = DEFAULT_PLATFORM_ENCODING;
        }
        this.platformEncoding = platformEncoding;
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        this.encoding = encoding;
        pbReader = new PushbackReader(new InputStreamReader(is, Charset.forName(encoding.getName())));
    }

    private int skipSpace() throws IOException {
        int ch;
        while (SymbolUtils.isSpace(ch = pbReader.read())) ;
        return ch;
    }

    private Lexem getEOS() {
        return new Lexem(lineNumber, LexemType.EOS);
    }

    private Lexem getEOL(int ch) {
        return new Lexem(lineNumber++, LexemType.EOL);
    }

    private Lexem getComment(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!SymbolUtils.isEOS(ch = pbReader.read())) {
            if (SymbolUtils.isEOL(ch)) {
                pbReader.unread(ch);
                break;
            }
            sb.append((char) ch);
        }
        return new Lexem(lineNumber, LexemType.COMMENT, sb.toString());
    }

    private Lexem getDottedIdentifier(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        ch = pbReader.read();
        if (!SymbolUtils.isLetter(ch) && !SymbolUtils.isUnderline(ch)) {
            throw new CompilerException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
        sb.append((char) ch);
        while (!SymbolUtils.isEOS(ch)) {
            ch = pbReader.read();
            if (SymbolUtils.isIdentifier(ch)) {
                sb.append((char) ch);
            } else {
                if (!SymbolUtils.isEOS(ch)) {
                    pbReader.unread(ch);
                }
                break;
            }
        }
        return new Lexem(lineNumber, LexemType.IDENTIFIER, sb.toString());
    }

    private Lexem getIdentifier(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        if (SymbolUtils.isHexDigit(ch)) {
            while (SymbolUtils.isHexDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
            if (SymbolUtils.isHexOldStylePostfix(ch)) {
                return new Lexem(lineNumber, LexemType.HEXADECIMAL, sb.toString());
            }
            if (!SymbolUtils.isEOS(ch)) {
                pbReader.unread(ch);
            }
        }
        LexemType lexemType = LexemType.IDENTIFIER;
        while (!SymbolUtils.isEOS(ch = pbReader.read())) {
            if (SymbolUtils.isIdentifier(ch)) {
                sb.append((char) ch);
            } else {
                if (SymbolUtils.isColon(ch)) {
                    lexemType = LexemType.LABEL;
                    break;
                }
                if (SymbolUtils.isApostrophe(ch)) {
                    sb.append((char) ch);
                    break;
                }
                if (!SymbolUtils.isEOS(ch)) {
                    pbReader.unread(ch);
                }
                break;
            }
        }
        return new Lexem(lineNumber, lexemType, sb.toString());
    }

    private Lexem getBinaryNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtils.isBinaryDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        if (!SymbolUtils.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.BINARY, sb.toString());
    }

    private Lexem getOctalNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtils.isOctalDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        if (!SymbolUtils.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.OCTAL, sb.toString());
    }

    private Lexem getHexadecimalNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtils.isHexDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        if (!SymbolUtils.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.HEXADECIMAL, sb.toString());
    }

    private Lexem getCheckedHexadecimalNumber(int ch) throws IOException {
        ch = pbReader.read();
        if (!SymbolUtils.isHexDigit(ch)) {
            throw new CompilerException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED)
                    , String.valueOf((char) ch));
        }
        return getHexadecimalNumber(ch);
    }

    private Lexem getNumber(int ch) throws IOException {
        if (ch == '0') {
            StringBuilder sb = new StringBuilder();
            sb.append((char) ch);
            ch = pbReader.read();
            if (SymbolUtils.isHexNewStylePrefix(ch)) {
                ch = pbReader.read();
                if (!SymbolUtils.isHexDigit(ch)) {
                    throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                            .INVALID_NUMBER_FORMAT), sb.toString());
                }
                return getHexadecimalNumber(ch);
            } else {
                if (SymbolUtils.isBinaryNewStylePrefix(ch)) {
                    ch = pbReader.read();
                    if (!SymbolUtils.isBinaryDigit(ch)) {
                        throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                                .INVALID_NUMBER_FORMAT), sb.toString());
                    }
                    return getBinaryNumber(ch);
                } else {
                    if (SymbolUtils.isOctalDigit(ch)) {
                        return getOctalNumber(ch);
                    } else {
                        if (SymbolUtils.isHexDigit(ch)) {
                            return getNumber(ch);
                        } else {
                            if (!SymbolUtils.isEOS(ch)) {
                                pbReader.unread(ch);
                            }
                            return new Lexem(lineNumber, LexemType.DECIMAL, sb.toString());
                        }
                    }
                }
            }
        }
        return getOldStyleNumber(ch);
    }

    private Lexem getOldStyleNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtils.isDecDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        String number = sb.toString();
        if (SymbolUtils.isBinaryOldStylePostfix(ch)) {
            if (!Checker.isBinaryNumber(number)) {
                throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                        .INVALID_BINARY_NUMBER_FORMAT), number);
            } else {
                return new Lexem(lineNumber, LexemType.BINARY, number);
            }
        }
        if (SymbolUtils.isOctalOldStylePostfix(ch)) {
            if (!Checker.isOctalNumber(number)) {
                throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                        .INVALID_OCTAL_NUMBER_FORMAT), number);
            } else {
                return new Lexem(lineNumber, LexemType.OCTAL, number);
            }
        }
        if (SymbolUtils.isHexOldStylePostfix(ch)) {
            if (!Checker.isHexadecimalNumber(number)) {
                throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                        .INVALID_HEXADECIMAL_NUMBER_FORMAT), number);
            } else {
                return new Lexem(lineNumber, LexemType.HEXADECIMAL, number);
            }
        }
        if (SymbolUtils.isHexDigit(ch)) {
            sb.append((char) ch);
            while (SymbolUtils.isHexDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
            number = sb.toString();
            if (SymbolUtils.isHexOldStylePostfix(ch)) {
                return new Lexem(lineNumber, LexemType.HEXADECIMAL, number);
            }
        }
        if (!Checker.isDecimalNumber(number)) {
            throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList.INVALID_NUMBER_FORMAT), number);
        }
        if (!SymbolUtils.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.DECIMAL, number);
    }

    private Lexem getHexadecimalNumberOrVariable(int ch) throws IOException {
        ch = pbReader.read();
        if (SymbolUtils.isDecDigit(ch)) {
            return getHexadecimalNumber(ch);
        }
        pbReader.unread(ch);
        return getVariable(ch);
    }

    private Lexem getVariable(int ch) throws IOException {
        ch = pbReader.read();//get next symbol
        if (!SymbolUtils.isLetter(ch) && !SymbolUtils.isUnderline(ch)) {
            throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (!SymbolUtils.isEOS(ch = pbReader.read())) {
            if (SymbolUtils.isUnderline(ch) || SymbolUtils.isLetter(ch) || SymbolUtils.isDecDigit(ch)) {
                sb.append((char) ch);
            } else {
                if (!SymbolUtils.isEOS(ch)) {
                    pbReader.unread(ch);
                }
                break;
            }
        }
        return new Lexem(lineNumber, LexemType.VARIABLE, sb.toString());
    }

    private Lexem getDelimiter(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        LexemType type = LexemType.UNKNOWN;
        switch (ch) {
            case '+':
                type = LexemType.PLUS;
                break;
            case '-':
                type = LexemType.MINUS;
                break;
            case '/':
                type = LexemType.SLASH;
                break;
            case '*':
                type = LexemType.STAR;
                break;
            case '(':
                type = LexemType.OPEN_BRACE;
                break;
            case ')':
                type = LexemType.CLOSED_BRACE;
                break;
            case ',':
                type = LexemType.COMMA;
                break;
            case '&':
                type = LexemType.AMPERSAND;
                break;
            case '|':
                type = LexemType.PIPE;
                break;
            case '%':
                type = LexemType.PERCENT;
                break;
            case '~':
                type = LexemType.TILDE;
                break;
            case '^':
                type = LexemType.CARET;
                break;
            case '<':
                ch = pbReader.read();
                switch (ch) {
                    case '<':
                        sb.append((char) ch);
                        type = LexemType.LSHIFT;
                        break;
                    default:
                        if (!SymbolUtils.isEOS(ch)) {
                            pbReader.unread(ch);
                            break;
                        }
                }
                break;
            case '>':
                ch = pbReader.read();
                switch (ch) {
                    case '>':
                        sb.append((char) ch);
                        type = LexemType.RSHIFT;
                        break;
                    default:
                        if (!SymbolUtils.isEOS(ch)) {
                            pbReader.unread(ch);
                            break;
                        }
                }
                break;

        }
        return new Lexem(lineNumber, type, sb.toString());
    }

    private Lexem getChar(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!SymbolUtils.isApostrophe(ch = pbReader.read())) {
            if (SymbolUtils.isEOS(ch) || SymbolUtils.isEOL(ch)) {
                throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "'");
            }
            if (ch == '\\') {
                sb.append((char) ch);
                ch = pbReader.read();
                if (SymbolUtils.isEOS(ch) || SymbolUtils.isEOL(ch)) {
                    throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "'");
                }
            }
            sb.append((char) ch);
        }
        String charValue = sb.toString();
        if (!charValue.isEmpty()) {
            charValue = StringEscapeUtils.unescapeJava(charValue);
        }
        if (!Checker.isValidEncoding(charValue, platformEncoding)) {
            throw new BadCharsetEncodingException(file, lineNumber, MessageList.getMessage(MessageList.BAD_CHARSET_ENCODING)
                    , platformEncoding.getName());
        }
        if (charValue.length() > 1) {
            throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.CHAR_TOO_LONG), charValue);
        }
        return new Lexem(lineNumber, LexemType.CHAR, charValue);
    }

    private Lexem getString(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        do {
            while (!SymbolUtils.isQuote(ch = pbReader.read())) {
                if (SymbolUtils.isEOS(ch) || SymbolUtils.isEOL(ch)) {
                    throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "\"");
                }
                if (ch == '\\') {
                    sb.append((char) ch);
                    ch = pbReader.read();
                    if (SymbolUtils.isEOS(ch) || SymbolUtils.isEOL(ch)) {
                        throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "\"");
                    }
                }
                sb.append((char) ch);
            }
            ch = pbReader.read();
            if (!SymbolUtils.isQuote(ch)) {
                if (!SymbolUtils.isEOS(ch)) {
                    pbReader.unread(ch);
                }
                break;
            }
        } while (true);
        String value = sb.toString();
        if (!value.isEmpty()) {
            value = StringEscapeUtils.unescapeJava(value);
        }
        if (!Checker.isValidEncoding(value, platformEncoding)) {
            throw new BadCharsetEncodingException(file, lineNumber, MessageList.getMessage(MessageList.BAD_CHARSET_ENCODING)
                    , platformEncoding.getName());
        }
        return new Lexem(lineNumber, LexemType.STRING, value);
    }

    protected Lexem getNext() throws IOException {
        int ch = pbReader.read();
        if (SymbolUtils.isSpace(ch)) {
            ch = skipSpace();
        }
        if (SymbolUtils.isEOS(ch)) {
            return getEOS();
        }
        if (SymbolUtils.isEOL(ch)) {
            return getEOL(ch);
        }
        if (SymbolUtils.isComment(ch)) {
            return getComment(ch);
        }
        if (SymbolUtils.isLetter(ch) || SymbolUtils.isUnderline(ch)) {
            return getIdentifier(ch);
        }
        if (SymbolUtils.isDot(ch)) {
            return getDottedIdentifier(ch);
        }
        if (SymbolUtils.isDecDigit(ch)) {
            return getNumber(ch);
        }
        if (SymbolUtils.isDelimiter(ch)) {
            return getDelimiter(ch);
        }
        if (SymbolUtils.isApostrophe(ch)) {
            return getChar(ch);
        }
        if (SymbolUtils.isQuote(ch)) {
            return getString(ch);
        }
        if (SymbolUtils.isDollar(ch)) {
            return getHexadecimalNumberOrVariable(ch);
        }
        if (SymbolUtils.isHash(ch)) {
            return getCheckedHexadecimalNumber(ch);
        }
        throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL)
                , String.valueOf((char) ch));
    }

    @Override
    public Iterator<Lexem> iterator() {
        if (lexemInternalIterator == null) {
            lexemInternalIterator = new LexemInternalIterator();
        }
        return lexemInternalIterator;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    private class LexemInternalIterator extends AnalyzerIterator<Lexem> {
        @Override
        protected Lexem externalNext() throws IOException {
            return getNext();
        }

        @Override
        protected boolean externalHasNext() {
            return lastItem.getType() != LexemType.EOS;
        }
    }
}
