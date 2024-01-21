package ru.assembler.core.compiler.dummy;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.compiler.option.OptionType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public class CompilerApiFreezed implements CompilerApi {
    private final int lineNumber;

    private final int compiledLineNumber;

    private final int compiledSourceNumber;

    private final File file;

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

    @Override
    public boolean hasOption(OptionType type) {
        return false;
    }

    @Override
    public Option getOption(@NonNull OptionType type) {
        return null;
    }

    @Override
    public boolean addOption(@NonNull Option option) {
        return false;
    }

    @Override
    public boolean addCommand(@NonNull String name, @NonNull CommandCompiler commandCompiler) {
        return false;
    }
}
