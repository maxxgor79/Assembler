package ru.zxspectrum.disassembler.lang.tree;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maxim
 * Date: 12/27/2023
 */
public class Node<E> {
    @Getter
    @Setter
    @NonNull
    private E content;

    private Map<ByteCodeUnit, Node> nodeMap = new HashMap<>();

    public Node() {

    }

    public Node(@NonNull E content) {
        this.content = content;
    }

    public boolean contains(ByteCodeUnit unit) {
        if (unit == null) {
            return false;
        }
        return nodeMap.containsKey(unit);
    }

    public void add(@NonNull ByteCodeUnit unit, @NonNull Node node) {
        nodeMap.put(unit, node);
    }

    public Node get(ByteCodeUnit unit) {
        if (unit == null) {
            return null;
        }
        return nodeMap.get(unit);
    }

    public int getNodeCount() {
        return nodeMap.size();
    }
}
