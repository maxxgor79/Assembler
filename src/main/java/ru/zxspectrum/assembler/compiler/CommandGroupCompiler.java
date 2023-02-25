package ru.zxspectrum.assembler.compiler;

/**
 * @Author Maxim Gorin
 */
public interface CommandGroupCompiler extends CommandCompiler {
    public boolean add(CommandCompiler compiler);
}
