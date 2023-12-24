package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.compiler.option.Option;
import ru.zxspectrum.assembler.compiler.option.OptionType;

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
}
