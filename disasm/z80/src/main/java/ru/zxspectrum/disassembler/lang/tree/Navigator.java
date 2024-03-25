package ru.zxspectrum.disassembler.lang.tree;

import lombok.NonNull;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;

/**
 * @author maxim
 * Date: 12/27/2023
 */
public class Navigator<E> {
    private Node<E> node;

    private Navigator() {

    }

    public Navigator(@NonNull Node<E> node) {
        this.node = node;
    }

    public boolean next(ByteCodeUnit unit) {
        if (unit == null) {
            return false;
        }
        Node<E> node = this.node.get(unit);
        if (node != null) {
            this.node = node;
            return true;
        }
        return false;
    }

    public E getContent() {
        return node.getContent();
    }

    public void setContent(@NonNull E content) {
        node.setContent(content);
    }

    public boolean contains(ByteCodeUnit unit) {
        return node.contains(unit);
    }

    public void add(ByteCodeUnit unit, Node node) {
        this.node.add(unit, node);
    }

    public Node<E> get(ByteCodeUnit unit) {
        return node.get(unit);
    }

    public int getNodeCount() {
        return node.getNodeCount();
    }

    public boolean isTerminal() {
        return node.getNodeCount() == 0 && node.getContent() != null;
    }
}
