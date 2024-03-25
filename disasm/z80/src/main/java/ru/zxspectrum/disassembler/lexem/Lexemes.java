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
public class Lexemes {
    private final List<Lexem> lexemes = new LinkedList<>();

    public Lexemes() {

    }

    public Lexemes(@NonNull Collection<Lexem> col) {
        lexemes.addAll(col);
    }

    public void clear() {
        lexemes.clear();
    }

    public void addAll(@NonNull Collection<Lexem> col) {
        lexemes.addAll(col);
    }

    public void add(@NonNull Lexemes lexemes) {
        this.lexemes.addAll(lexemes.lexemes);
    }

    public Collection<Lexem> toCollection() {
        return lexemes;
    }

    public Lexem getVariable(int num) {
        int i = 0;
        for (Lexem l : lexemes) {
            if ((l.getType() == LexemType.Variable) && (num == i++)) {
                return l;
            }
        }
        return null;
    }

    public int getVariableCount() {
        int count = 0;
        for (Lexem l : lexemes) {
            if (l.getType() == LexemType.Variable) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return lexemes.size();
    }
}
