package ru.assembler.core.lexem;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import ru.assembler.core.error.BadCharsetEncodingException;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.InvalidFormatNumberException;
import ru.assembler.core.error.LexemException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lang.Encoding;
import ru.assembler.core.util.AnalyzerIterator;
import ru.assembler.core.util.LexemUtil;
import ru.assembler.core.util.SymbolUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public class LexemAnalyzer implements Iterable<Lexem> {
    private final static Encoding DEFAULT_ENCODING = Encoding.UTF_8;

    private final static Encoding DEFAULT_PLATFORM_ENCODING = Encoding.ASCII;


    private PushbackReader pbReader;

    private int lineNumber = 1;

    private File file;

    private LexemInternalIterator lexemInternalIterator;

    @Getter
    private Encoding encoding;

    private Encoding platformEncoding;

    private LexemAnalyzer() {

    }

    public LexemAnalyzer(File file, @NonNull InputStream is) {
        this(file, is, DEFAULT_PLATFORM_ENCODING, DEFAULT_ENCODING);
    }

    public LexemAnalyzer(File file, @NonNull InputStream is, Encoding platformEncoding) {
        this(file, is, platformEncoding, DEFAULT_ENCODING);
    }

    public LexemAnalyzer(File file, @NonNull InputStream is, Encoding platformEncoding, Encoding encoding) {
        this.file = file;
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
        while (SymbolUtil.isSpace(ch = pbReader.read())) ;
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
        while (!SymbolUtil.isEOS(ch = pbReader.read())) {
            if (SymbolUtil.isEOL(ch)) {
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
        if (!SymbolUtil.isLetter(ch) && !SymbolUtil.isUnderline(ch)) {
            throw new CompilerException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
        sb.append((char) ch);
        while (!SymbolUtil.isEOS(ch)) {
            ch = pbReader.read();
            if (SymbolUtil.isIdentifier(ch)) {
                sb.append((char) ch);
            } else {
                if (!SymbolUtil.isEOS(ch)) {
                    pbReader.unread(ch);
                }
                break;
            }
        }
        return new Lexem(lineNumber, LexemType.IDENTIFIER, sb.toString());
    }

    private Lexem getIdentifier(int ch) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (!SymbolUtil.isEOS(ch = pbReader.read())) {
            if (SymbolUtil.isLetter(ch) || SymbolUtil.isDecDigit(ch) || SymbolUtil.isUnderline(ch)) {
                sb.append((char) ch);
            } else {
                break;
            }
        }
        if (SymbolUtil.isColon(ch)) {
            return new Lexem(lineNumber, LexemType.LABEL, sb.toString());
        } else if (SymbolUtil.isApostrophe(ch)) {
            sb.append((char) ch);
        } else {
            if (!SymbolUtil.isEOS(ch)) {
                pbReader.unread(ch);
            }
        }
        final String name = sb.toString();
        if (SymbolUtil.isHexOldStylePostfix(name.charAt(name.length() - 1))) {
            final String number = StringUtils.chop(name);
            if (LexemUtil.isHexNumber(number)) {
                return new Lexem(lineNumber, LexemType.HEXADECIMAL, number);
            }
        }
        return new Lexem(lineNumber, LexemType.IDENTIFIER, name);
    }

    private Lexem getBinaryNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtil.isHexDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        final String number = sb.toString();
        if (!Checker.isBinaryNumber(number)) {
            throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                    .INVALID_BINARY_NUMBER_FORMAT), number);
        }
        if (!SymbolUtil.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.BINARY, number);
    }

    private Lexem getHexadecimalNumber(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (SymbolUtil.isHexDigit(ch = pbReader.read())) {
            sb.append((char) ch);
        }
        if (!SymbolUtil.isEOS(ch)) {
            pbReader.unread(ch);
        }
        return new Lexem(lineNumber, LexemType.HEXADECIMAL, sb.toString());
    }

    private Lexem getCheckedHexadecimalNumber(int ch) throws IOException {
        ch = pbReader.read();
        if (!SymbolUtil.isHexDigit(ch)) {
            throw new CompilerException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED)
                    , String.valueOf((char) ch));
        }
        return getHexadecimalNumber(ch);
    }

    private Lexem getNumber(int ch) throws IOException {
        final int oldCh = ch;
        if (ch == '0') {
            final StringBuilder sb = new StringBuilder();
            sb.append((char) ch);
            ch = pbReader.read();
            if (SymbolUtil.isHexNewStylePrefix(ch)) {
                ch = pbReader.read();
                if (!SymbolUtil.isHexDigit(ch)) {
                    throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                            .INVALID_NUMBER_FORMAT), sb.toString());
                }
                return getHexadecimalNumber(ch);
            } else {
                if (SymbolUtil.isBinaryNewStylePrefix(ch)) {
                    ch = pbReader.read();
                    if (!SymbolUtil.isBinaryDigit(ch)) {
                        throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                                .INVALID_NUMBER_FORMAT), sb.toString());
                    }
                    return getBinaryNumber(ch);
                } else {
                    if (!SymbolUtil.isEOS(ch)) {
                        pbReader.unread(ch);
                    }
                    ch = oldCh;
                }
            }
        }
        return getOldStyleNumber(ch);
    }

    private Lexem getOldStyleNumber(int ch) throws IOException {
        LexemType lexemType = (ch == '0') ? LexemType.OCTAL : LexemType.DECIMAL;
        final StringBuilder sb = new StringBuilder();
        String number;
        if (SymbolUtil.isBinaryDigit(ch)) {
            sb.append((char) ch);
            while (SymbolUtil.isBinaryDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
            if (SymbolUtil.isBinaryOldStylePostfix(ch)) {
                number = sb.toString();
                return new Lexem(lineNumber, LexemType.BINARY, number);
            }
        }
        if (SymbolUtil.isOctalDigit(ch)) {
            sb.append((char) ch);
            while (SymbolUtil.isOctalDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
        }
        if (SymbolUtil.isDecDigit(ch)) {
            lexemType = LexemType.DECIMAL;
            sb.append((char) ch);
            while (SymbolUtil.isDecDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
        }
        if (SymbolUtil.isHexDigit(ch)) {
            sb.append((char) ch);
            while (SymbolUtil.isHexDigit(ch = pbReader.read())) {
                sb.append((char) ch);
            }
        }
        number = sb.toString();
        if (SymbolUtil.isOctalOldStylePostfix(ch)) {
            if (!Checker.isOctalNumber(number)) {
                throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                        .INVALID_OCTAL_NUMBER_FORMAT), number);
            } else {
                return new Lexem(lineNumber, LexemType.OCTAL, number);
            }
        }
        if (SymbolUtil.isHexOldStylePostfix(ch)) {
            if (!Checker.isHexadecimalNumber(number)) {
                throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                        .INVALID_HEXADECIMAL_NUMBER_FORMAT), number);
            } else {
                return new Lexem(lineNumber, LexemType.HEXADECIMAL, number);
            }
        }
        if (!SymbolUtil.isEOS(ch)) {
            pbReader.unread(ch);
        }
        switch (lexemType) {
            case DECIMAL:
                if (!Checker.isDecimalNumber(number)) {
                    throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                            .INVALID_NUMBER_FORMAT), number);
                }
                break;
            case OCTAL:
                if (!Checker.isOctalNumber(number)) {
                    throw new InvalidFormatNumberException(file, lineNumber, MessageList.getMessage(MessageList
                            .INVALID_OCTAL_NUMBER_FORMAT), number);
                }
                break;
        }
        return new Lexem(lineNumber, lexemType, number);
    }

    private Lexem getHexadecimalNumberOrVariable(int ch) throws IOException {
        ch = pbReader.read();
        if (SymbolUtil.isDecDigit(ch)) {
            return getHexadecimalNumber(ch);
        }
        if (SymbolUtil.isLetter(ch) || SymbolUtil.isUnderline(ch)) {
            return getVariable(ch);
        } else {
            throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
    }

    private Lexem getVariable(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (!SymbolUtil.isEOS(ch = pbReader.read())) {
            if (SymbolUtil.isUnderline(ch) || SymbolUtil.isLetter(ch) || SymbolUtil.isDecDigit(ch)) {
                sb.append((char) ch);
            } else {
                if (!SymbolUtil.isEOS(ch)) {
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
                        if (!SymbolUtil.isEOS(ch)) {
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
                        if (!SymbolUtil.isEOS(ch)) {
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
        while (!SymbolUtil.isApostrophe(ch = pbReader.read())) {
            if (SymbolUtil.isEOS(ch) || SymbolUtil.isEOL(ch)) {
                throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "'");
            }
            if (ch == '\\') {
                sb.append((char) ch);
                ch = pbReader.read();
                if (SymbolUtil.isEOS(ch) || SymbolUtil.isEOL(ch)) {
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
            while (!SymbolUtil.isQuote(ch = pbReader.read())) {
                if (SymbolUtil.isEOS(ch) || SymbolUtil.isEOL(ch)) {
                    throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "\"");
                }
                if (ch == '\\') {
                    sb.append((char) ch);
                    ch = pbReader.read();
                    if (SymbolUtil.isEOS(ch) || SymbolUtil.isEOL(ch)) {
                        throw new LexemException(file, lineNumber, MessageList.getMessage(MessageList.EXPECTED_SYMBOL), "\"");
                    }
                }
                sb.append((char) ch);
            }
            ch = pbReader.read();
            if (!SymbolUtil.isQuote(ch)) {
                if (!SymbolUtil.isEOS(ch)) {
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
        if (SymbolUtil.isSpace(ch)) {
            ch = skipSpace();
        }
        if (SymbolUtil.isEOS(ch)) {
            return getEOS();
        }
        if (SymbolUtil.isEOL(ch)) {
            return getEOL(ch);
        }
        if (SymbolUtil.isComment(ch)) {
            return getComment(ch);
        }
        if (SymbolUtil.isLetter(ch) || SymbolUtil.isUnderline(ch)) {
            return getIdentifier(ch);
        }
        if (SymbolUtil.isDot(ch)) {
            return getDottedIdentifier(ch);
        }
        if (SymbolUtil.isDecDigit(ch)) {
            return getNumber(ch);
        }
        if (SymbolUtil.isDelimiter(ch)) {
            return getDelimiter(ch);
        }
        if (SymbolUtil.isApostrophe(ch)) {
            return getChar(ch);
        }
        if (SymbolUtil.isQuote(ch)) {
            return getString(ch);
        }
        if (SymbolUtil.isDollar(ch)) {
            return getHexadecimalNumberOrVariable(ch);
        }
        if (SymbolUtil.isHash(ch)) {
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
