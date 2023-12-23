package ru.zxspectrum.assembler.compiler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public interface CompilerApi {
    public int getLineNumber();

    public void compile() throws IOException;

    public int getCompiledLineCount();

    public int addCompiledLineCount(int lineCount);

    public int getCompiledSourceCount();

    public int addCompiledSourceCount(int sourceCount);

    public String getFileName();

    public File getFile();

    public OutputStream getOutputStream();
}
