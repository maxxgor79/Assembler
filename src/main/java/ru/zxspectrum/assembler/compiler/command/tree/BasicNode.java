package ru.zxspectrum.assembler.compiler.command.tree;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.lexem.Lexem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
public class BasicNode implements Node {
    protected CommandCompiler compiler;
    protected final Map<Lexem, Node> linkMap = new HashMap<>();

    BasicNode(CommandCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public boolean isTerminal(@NonNull Lexem lexem) {
        final Node nextNode = linkMap.get(lexem);
        Objects.requireNonNull(nextNode);
        return nextNode.size() == 0;
    }

    @Override
    public Node next(@NonNull Lexem lexem) {
        Node nextNode = linkMap.get(lexem);
        Objects.requireNonNull(nextNode);
        return nextNode;
    }

    @Override
    public void add(@NonNull Lexem lexem, @NonNull Node node) {
        if (linkMap.containsKey(lexem)) {
            final Node nextNode = linkMap.get(lexem);
            Objects.requireNonNull(nextNode);
            nextNode.merge(node);
        } else {
            linkMap.put(lexem, node);
        }
    }

    @Override
    public void replace(@NonNull Lexem lexem, @NonNull Node node) {
        final Node nextNode = linkMap.get(lexem);
        Objects.requireNonNull(nextNode);
        if (!(nextNode instanceof BasicNode) || !(node instanceof BasicNode)) {
            throw new UnsupportedOperationException();
        }
        ((BasicNode) nextNode).compiler = ((BasicNode) node).compiler;
    }


    @Override
    public int size() {
        return linkMap.size();
    }

    @Override
    public boolean contains(Lexem lexem) {
        if (lexem == null) {
            return false;
        }
        return linkMap.containsKey(lexem);
    }

    @Override
    public CommandCompiler getCompiler(Lexem lexem) {
        if (lexem == null) {
            return compiler;
        }
        final Node nextNode = linkMap.get(lexem);
        Objects.requireNonNull(nextNode);
        return nextNode.getCompiler(null);
    }

    @Override
    public void merge(@NonNull Node node) {
        if (!(node instanceof BasicNode)) {
            throw new UnsupportedOperationException();
        }
        final Map<Lexem, Node> nodeLinkMap = ((BasicNode) node).linkMap;
        for (Map.Entry<Lexem, Node> entry : nodeLinkMap.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }
}
