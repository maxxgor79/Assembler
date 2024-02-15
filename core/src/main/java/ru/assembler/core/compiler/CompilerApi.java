package ru.assembler.core.compiler;

import java.io.FileNotFoundException;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.compiler.option.OptionType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public interface CompilerApi {

  int getLineNumber();

  void compile() throws IOException;

  int getCompiledLineCount();

  int addCompiledLineCount(int lineCount);

  int getCompiledSourceCount();

  int addCompiledSourceCount(int sourceCount);

  String getFileName();

  File getFile();

  OutputStream getOutputStream();

  boolean hasOption(OptionType type);

  Option getOption(OptionType type);

  boolean addOption(Option option);

  boolean addCommand(String name, CommandCompiler commandCompiler);

  void stop();

  boolean isStopped();

  boolean include(String path) throws IOException;
}
