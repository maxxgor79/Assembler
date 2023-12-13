package ru.zxspectrum.assembler.compiler;

/**
 * @Author Maxim Gorin
 */
public interface CommandGroupCompiler extends CommandCompiler {
    boolean add(CommandCompiler compiler);
}
