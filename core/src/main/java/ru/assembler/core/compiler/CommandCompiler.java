package ru.assembler.core.compiler;

import lombok.NonNull;
import ru.assembler.core.syntax.LexemSequence;

/**
 * @author Maxim Gorin
 */
public interface CommandCompiler {

  String[] names();

  byte[] compile(LexemSequence lexemSequence);

  default boolean contains(@NonNull String[] nameList, @NonNull String commandName) {
    if (commandName == null) {
      return false;
    }
    for (final String name : nameList) {
      if (commandName.equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }
}
