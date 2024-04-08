package ru.zxspectrum.disassembler.lexem;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author maxim
 * Date: 12/29/2023
 */
@EqualsAndHashCode
public class Lexemes implements Cloneable {
    private final List<Lexeme> lexemes = new LinkedList<>();

    public Lexemes() {

    }

    public Lexemes(@NonNull Collection<Lexeme> col) {
        lexemes.addAll(col);
    }

    public void clear() {
        lexemes.clear();
    }

    public void addAll(@NonNull Collection<Lexeme> col) {
        lexemes.addAll(col);
    }

    public void add(@NonNull Lexemes lexemes) {
        this.lexemes.addAll(lexemes.lexemes);
    }

    public Collection<Lexeme> getLexemes() {
        return lexemes;
    }

    public Lexeme getVariable(int num) {
        int i = 0;
        for (Lexeme l : lexemes) {
            if ((l.getType() == LexemType.Variable) && (num == i++)) {
                return l;
            }
        }
        return null;
    }

    public int getVariableCount() {
        int count = 0;
        for (Lexeme l : lexemes) {
            if (l.getType() == LexemType.Variable) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return lexemes.size();
    }

    @Override
    public Lexemes clone() {
        return new Lexemes(getLexemes());
    }
}
