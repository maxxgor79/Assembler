package ru.zxspectrum.basic.compile;

import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.LexemType;
import ru.zxspectrum.basic.Parser;
import ru.zxspectrum.basic.ParserException;
import ru.zxspectrum.basic.SymbolUtil;

import java.io.IOException;

/**
 * @author Maxim Gorin
 */
public class SourceParser extends Parser {

  @Override
  public Lexem next() throws ParserException, IOException {
    Lexem lexem = null;
    while (true) {
      final int ch = is.read();
      if (ch == -1) {
        return lexem;
      }
      if (SymbolUtil.isTextWhite(ch)) {
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
      } else if (SymbolUtil.isDollar(ch)) {
        lexem = getVariable(ch);
      } else {
        lexem = getSymbol(ch);
      }
      return lexem;
    }
  }

  private Lexem getVariable(int ch) throws ParserException, IOException {
    StringBuilder sb = new StringBuilder();
    ch = is.read();
    if (SymbolUtil.isAlphabet(ch) || SymbolUtil.isUnderline(ch)) {
      sb.append((char) ch);
      while ((ch = is.read()) != -1) {
        if (SymbolUtil.isAlphabet(ch) || SymbolUtil.isDigit(ch) || SymbolUtil.isUnderline(ch)) {
          sb.append((char) ch);
        } else {
          is.unread(ch);
          break;
        }
      }
    } else {
      throw new ParserException("Literal is required");
    }
    return new Lexem(LexemType.Variable, sb.toString());
  }

  @Override
  protected Lexem getDelimiter(int ch) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append((char) ch);
    if (ch == '<') {
      ch = is.read();
      switch (ch) {
        case '>':
        case '=':
          sb.append((char) ch);
          break;
        case -1:
          break;
        default:
          is.unread(ch);
          break;
      }
    } else if (ch == '>') {
      ch = is.read();
      switch (ch) {
        case '=':
          sb.append((char) ch);
          break;
        case -1:
          break;
        default:
          is.unread(ch);
          break;
      }
    }
    return new Lexem(LexemType.Delimiter, sb.toString());
  }

  @Override
  protected Lexem getInteger(int ch) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append((char) ch);
    while ((ch = is.read()) != -1) {
      if (SymbolUtil.isDigit(ch)) {
        sb.append((char) ch);
      } else {
        is.unread(ch);
        break;
      }
    }
    Lexem lexem = new Lexem(LexemType.Number, sb.toString());
    lexem.setIntValue(Integer.valueOf(sb.toString()));
    return lexem;
  }

  @Override
  protected Lexem getEol(int ch) throws IOException {
    if (ch == '\r') {
      ch = is.read();
      switch (ch) {
        case -1:
        case '\n':
          break;
        default:
          is.unread(ch);
          break;
      }
    }
    return new Lexem(LexemType.Eol, EOL);
  }

  @Override
  protected boolean isEol(int ch) {
    return SymbolUtil.isTextEol(ch);
  }

  protected static boolean isDelimiter(int ch) {
    return "+-*/()<>=:".indexOf(ch) != -1;
  }
}
