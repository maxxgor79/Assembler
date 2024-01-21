package ru.assembler.core.compiler.command.tree;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.lexem.Lexem;

/**
 * @author Maxim Gorin
 */
class Navigator {
    protected Node current;

    Navigator(@NonNull Node current) {
        this.current = current;
    }

    public boolean isTerminal(Lexem lexem) {
        return current.isTerminal(lexem);
    }

    public boolean next(Lexem lexem) {
        Node node = current.next(lexem);
        if (node != null) {
            current = node;
            return true;
        }
        return false;
    }

    public void add(@NonNull Lexem lexem, @NonNull Node node) {
        current.add(lexem, node);
    }

    public void replace(@NonNull Lexem lexem, @NonNull Node node) {
        current.replace(lexem, node);
    }

    public boolean contains(Lexem lexem) {
        return current.contains(lexem);
    }

    public CommandCompiler getCompiler(Lexem lexem) {
        return current.getCompiler(lexem);
    }
}
