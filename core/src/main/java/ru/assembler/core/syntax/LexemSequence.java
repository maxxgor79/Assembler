package ru.assembler.core.syntax;

import java.io.File;
import lombok.NonNull;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemAnalyzer;
import ru.assembler.core.lexem.LexemType;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
public class LexemSequence {

  private List<Lexem> lexemSequence;

  private LexemSequence() {

  }

  public LexemSequence(@NonNull List<Lexem> lexemSequence) {
    set(lexemSequence);
  }

  public LexemSequence(@NonNull Lexem... lexemSequence) {
    set(List.of(lexemSequence));
  }

  public LexemSequence(@NonNull Iterator<Lexem> iterator) {
    lexemSequence = new ArrayList<>();
    while (iterator.hasNext()) {
      lexemSequence.add(iterator.next());
    }
  }

  public LexemSequence(@NonNull String mask) {
    if (mask.trim().isEmpty()) {
      throw new IllegalArgumentException("mask is null or empty");
    }
    LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(new ByteArrayInputStream(mask.getBytes()));
    List<Lexem> seq = new LinkedList<>();
    for (Lexem t : lexemAnalyzer) {
      seq.add(t);
    }
    if (seq.isEmpty()) {
      throw new IllegalArgumentException("mask does not contain anything!");
    }
    set(seq);
  }

  protected void set(@NonNull Collection<Lexem> seq) {
    if (seq.isEmpty()) {
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
    return !lexemSequence.isEmpty() ? lexemSequence.get(0) : null;
  }

  public int getLineNumber() {
    if (lexemSequence.isEmpty()) {
      return -1;
    }
    return first().getLineNumber();
  }

  public File getFile() {
    if (lexemSequence.isEmpty()) {
      return null;
    }
    return first().getFile();
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

  public Lexem get(int index) {
    return lexemSequence.get(index);
  }

  public void set(int index, @NonNull Lexem lexem) {
    lexemSequence.set(index, lexem);
  }

  public Collection<Lexem> getVariables() {
    final List<Lexem> variables = new ArrayList<>();
    for (int i = 0; i < lexemSequence.size(); i++) {
      final Lexem lexem = lexemSequence.get(i);
      if (lexem.getType() == LexemType.VARIABLE) {
        variables.add(lexem);
      }
    }
    return Collections.unmodifiableList(variables);
  }

  public boolean hasVariables() {
    return !getVariables().isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
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
