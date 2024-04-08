package ru.zxspectrum.disassembler.render.command;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.lexem.Lexeme;
import ru.zxspectrum.disassembler.lexem.LexemType;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

/**
 * @author maxim
 * Date: 1/3/2024
 */
@Slf4j
public final class Formatter {
    private static char SPACE = ' ';

    private Formatter() {

    }

    public static void format(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        switch (lexem.getType()) {
            case Number -> formatNumber(sb, prevLexem, lexem);
            case String -> formatString(sb, prevLexem, lexem);
            case Variable -> formatVariable(sb, prevLexem, lexem);
            case Symbol -> formatSymbol(sb, prevLexem, lexem);
            case Identifier -> formatIdentifier(sb, prevLexem, lexem);
            case Delimiter -> formatDelimiter(sb, prevLexem, lexem);
            default -> sb.append(lexem);
        }
    }

    protected static void formatDelimiter(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        final String s = lexem.getValue();
        if (SymbolUtils.isComma(s)) {
            sb.append(lexem);
            sb.append(SPACE);
        } else if (SymbolUtils.isPlus(s) || SymbolUtils.isMinus(s) || SymbolUtils.isEqual(s)) {
            sb.append(SPACE);
            sb.append(lexem);
            sb.append(SPACE);
        } else if (SymbolUtils.isBraceOpen(s)) {
            if (prevLexem != null) {
                if (prevLexem.getType() == LexemType.Number || prevLexem.getType() == LexemType.Identifier ||
                        prevLexem.getType() == LexemType.Variable) {
                    sb.append(SPACE);
                }
            }
            sb.append(lexem);
        } else {
            sb.append(lexem);
        }
    }

    protected static void formatIdentifier(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        if (prevLexem != null) {
            if (prevLexem.getType() == LexemType.Number || prevLexem.getType() == LexemType.Identifier ||
                    prevLexem.getType() == LexemType.Variable) {
                sb.append(SPACE);
            }
        }
        sb.append(lexem);
    }

    protected static void formatSymbol(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        sb.append(lexem);
    }

    protected static void formatVariable(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        if ((prevLexem != null)) {
            if (prevLexem.getType() == LexemType.Identifier || prevLexem.getType() == LexemType.Number ||
                    prevLexem.getType() == LexemType.Variable)
                sb.append(SPACE);
        }
    }

    protected static void formatString(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexem) {
        sb.append(lexem);
    }

    protected static void formatNumber(@NonNull StringBuilder sb, Lexeme prevLexem, @NonNull Lexeme lexeme) {
        if ((prevLexem != null) && (prevLexem.getType() == LexemType.Identifier)) {
            sb.append(SPACE);
        }
        sb.append(lexeme);
    }
}
