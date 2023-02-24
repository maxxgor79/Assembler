package ru.zxspectrum.assembler.syntax;

import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemAnalyzer;
import ru.zxspectrum.assembler.lexem.LexemType;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Maxim Gorin
 */
public class LexemSequence {
    private List<Lexem> lexemSequence;

    private LexemSequence() {

    }

    public LexemSequence(List<Lexem> lexemSequence) {
        set(lexemSequence);
    }

    public LexemSequence(Lexem... lexemSequence) {
        set(List.of(lexemSequence));
    }

    public LexemSequence(Iterator<Lexem> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator is null");
        }
        lexemSequence = new LinkedList<>();
        while (iterator.hasNext()) {
            lexemSequence.add(iterator.next());
        }
    }

    public LexemSequence(String mask) {
        if (mask == null || mask.trim().isEmpty()) {
            throw new IllegalArgumentException("mask is null or empty");
        }
        LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(null, new ByteArrayInputStream(mask.getBytes()));
        List<Lexem> seq = new LinkedList<>();
        for (Lexem t : lexemAnalyzer) {
            seq.add(t);
        }
        if (seq.isEmpty()) {
            throw new IllegalArgumentException("mask does not contain anything!");
        }
        set(seq);
    }

    protected void set(Collection<Lexem> seq) {
        if (seq == null || seq.isEmpty()) {
            throw new IllegalArgumentException("seq is null or empty");
        }
        this.lexemSequence = new LinkedList<>(seq);
    }

    public Collection<Lexem> get() {
        if (lexemSequence == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(lexemSequence);
    }

    public Lexem first() {
        return lexemSequence.get(0);
    }

    public int getLineNumber() {
        if (lexemSequence.isEmpty()) {
            return -1;
        }
        return first().getLineNumber();
    }

    public String getCaption() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lexemSequence.size(); i++) {
            Lexem lexem = lexemSequence.get(i);
            if (lexem.getType() != LexemType.COMMA) {
                sb.append(" ");
            }
            sb.append(lexem.getValue());
        }
        return sb.toString().trim();
    }

    public int size() {
        return lexemSequence.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexemSequence that = (LexemSequence) o;
        return lexemSequence.equals(that.lexemSequence);
    }

    @Override
    public String toString() {
        return "LexemSequence{" +
                "seq=" + lexemSequence +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexemSequence);
    }
}
