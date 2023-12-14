package ru.zxspectrum.assembler.compiler.dummy;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.CompilerApi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author Maxim Gorin
 */
public class CompilerApiFreezed implements CompilerApi {
    private CompilerApi compilerApi;

    private int lineNumber;

    private int compiledLineNumber;

    private int compiledSourceNumber;

    private File file;

    public CompilerApiFreezed(@NonNull CompilerApi compilerApi) {
        lineNumber = compilerApi.getLineNumber();
        compiledLineNumber = compilerApi.getCompiledLineCount();
        compiledSourceNumber = compilerApi.getCompiledSourceCount();
        file = compilerApi.getFile();
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void compile() throws IOException {

    }

    @Override
    public int getCompiledLineCount() {
        return compiledLineNumber;
    }

    @Override
    public int addCompiledLineCount(int lineCount) {
        return compiledLineNumber;
    }

    @Override
    public int getCompiledSourceCount() {
        return compiledSourceNumber;
    }

    @Override
    public int addCompiledSourceCount(int sourceCount) {
        return compiledSourceNumber;
    }

    @Override
    public String getFileName() {
        if (file == null) {
            return null;
        }
        return file.getName();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }
}
