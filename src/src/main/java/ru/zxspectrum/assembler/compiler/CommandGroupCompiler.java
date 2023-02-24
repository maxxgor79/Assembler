package ru.zxspectrum.assembler.compiler;

public interface CommandGroupCompiler extends CommandCompiler {
    public boolean add(CommandCompiler compiler);
}
