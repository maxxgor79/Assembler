package ru.zxspectrum.assembler.compiler.command.tree;

import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.lexem.Lexem;

/**
 * @author Maxim Gorin
 */
public interface Node {
    boolean isTerminal(Lexem lexem);

    Node next(Lexem lexem);

    void add(Lexem lexem, Node node);

    void replace(Lexem lexem, Node node);

    int size();

    boolean contains(Lexem lexem);

    CommandCompiler getCompiler(Lexem lexem);

    void merge(Node node);
}
