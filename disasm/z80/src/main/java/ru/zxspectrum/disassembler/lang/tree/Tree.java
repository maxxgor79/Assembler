package ru.zxspectrum.disassembler.lang.tree;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnits;
import ru.zxspectrum.disassembler.bytecode.CodePatternParser;

/**
 * @author maxim
 * Date: 12/27/2023
 */
@Slf4j
public class Tree<E> {
    private final Node<E> rootNode = new Node<>();

    private static final CodePatternParser CODE_PATTERN_PARSER = new CodePatternParser();

    public Tree() {

    }

    public Navigator<E> getNavigator() {
        return new Navigator(rootNode);
    }

    public void add(@NonNull String codePattern, @NonNull E factory) {
        Navigator<E> navigator = getNavigator();
        CODE_PATTERN_PARSER.setCodePattern(codePattern);
        ByteCodeUnits byteCodeUnits = CODE_PATTERN_PARSER.parse();
        for (ByteCodeUnit unit : byteCodeUnits.toCollection()) {
            if (!navigator.contains(unit)) {
                navigator.add(unit, new Node<>());
            }
            navigator.next(unit);
        }
        navigator.setContent(factory);
    }
}
