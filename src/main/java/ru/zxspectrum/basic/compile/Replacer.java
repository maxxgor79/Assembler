package ru.zxspectrum.basic.compile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ru.zxspectrum.basic.SymbolUtil;

/**
 * Replacer.
 *
 * @author Maxim Gorin
 */
@ToString
@EqualsAndHashCode
public class Replacer {

  private Map<String, String> varMap = new HashMap<>();

  public Replacer() {

  }

  public Replacer(@NonNull final Map<String, String> varMap) {
    for (Map.Entry<String, String> entry : varMap.entrySet()) {
      add(entry.getKey(), entry.getValue());
    }
  }

  public boolean isEmpty() {
    return varMap.isEmpty();
  }

  private static boolean isValid(String varName) {
    for (int i = 0; i < varName.length(); i++) {
      int ch = varName.charAt(i);
      if (!SymbolUtil.isAlphabet(ch)) {
        return false;
      }
    }
    return true;
  }

  public Replacer add(@NonNull final String varName, @NonNull final String value) {
    if (varName.trim().isEmpty() || !isValid(varName)) {
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

  public String getValue(final String varName) {
    if (varName == null) {
      return null;
    }
    return varMap.get(varName);
  }
}
