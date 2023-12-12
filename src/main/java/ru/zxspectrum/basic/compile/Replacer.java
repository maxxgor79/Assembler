package ru.zxspectrum.basic.compile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.SymbolUtil;

/**
 * Replacer.
 *
 * @author Maxim Gorin
 */
@ToString
@EqualsAndHashCode
public class Replacer {

  private Map<String, Lexem> varMap = new HashMap<>();

  public Replacer() {

  }

  public Replacer(@NonNull final Map<String, Lexem> varMap) {
    for (Map.Entry<String, Lexem> entry : varMap.entrySet()) {
      add(entry.getKey(), entry.getValue());
    }
  }

  public boolean isEmpty() {
    return varMap.isEmpty();
  }

  private static boolean isValid(String varName) {
    if (varName == null || varName.trim().isEmpty()) {
      return false;
    }
    byte[] data = varName.getBytes();
    if (SymbolUtil.isUnderline(data[0]) || SymbolUtil.isAlphabet(data[0])) {
      for (int i = 1; i < data.length; i++) {
        if (!SymbolUtil.isAlphabet(data[i]) && !SymbolUtil.isDigit(data[i])
            && !SymbolUtil.isUnderline(data[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public Replacer add(@NonNull final String varName, @NonNull final Lexem value) {
    if (!isValid(varName)) {
      throw new IllegalArgumentException("varName='" + varName + "'");
    }
    varMap.put(varName, value);
    return this;
  }

  public boolean remove(final String varName) {
    return varMap.remove(varName) != null;
  }

  public Collection<String> variables() {
    return Collections.unmodifiableCollection(varMap.keySet());
  }

  public boolean contains(final String varName) {
    return varMap.containsKey(varName);
  }

  public Lexem getValue(final String varName) {
    if (varName == null) {
      return null;
    }
    return varMap.get(varName);
  }
}
