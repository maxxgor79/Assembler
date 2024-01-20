package ru.zxspectrum.assembler.compiler.command.tree;

import lombok.NonNull;
import org.apache.commons.collections4.iterators.PushbackIterator;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Maxim Gorin
 */
public class CommandTree {
    protected static final Lexem VAR_D = new Lexem(1, LexemType.VARIABLE, "d");

    protected static final Lexem VAR_DD = new Lexem(1, LexemType.VARIABLE, "dd");

    protected static final Lexem VAR_E = new Lexem(1, LexemType.VARIABLE, "e");

    protected static final Lexem VAR_EE = new Lexem(1, LexemType.VARIABLE, "ee");

    protected static final Lexem VAR_N = new Lexem(1, LexemType.VARIABLE, "n");

    protected static final Lexem VAR_NN = new Lexem(1, LexemType.VARIABLE, "nn");

    protected static final Lexem VAR_NNNN = new Lexem(1, LexemType.VARIABLE, "nnnn");


    private final BasicNode rootNode;

    public CommandTree() {
        rootNode = new BasicNode(null);
    }

    public Navigator getNavigator() {
        return new Navigator(rootNode);
    }

    public boolean isEmpty() {
        return getNodeCount() > 0;
    }

    public int getNodeCount() {
        return getNodeCount(rootNode);
    }

    protected int getNodeCount(Node node) {
        BasicNode basicNode = (BasicNode) node;
        int count = 0;
        for (Map.Entry<Lexem, Node> entry : basicNode.linkMap.entrySet()) {
            count += getNodeCount(entry.getValue());
        }
        return count;
    }

    public void add(@NonNull LexemSequence lexemSequence, CommandCompiler compiler) {
        final Navigator navigator = getNavigator();
        Iterator<Lexem> iter = lexemSequence.get().iterator();
        while (iter.hasNext()) {
            final Lexem lexem = iter.next();
            if (!navigator.contains(lexem)) {
                navigator.add(lexem, new BasicNode(compiler));
            } else {
                if (!iter.hasNext()) {
                    navigator.replace(lexem, new BasicNode(compiler));//replace compiler
                    continue;
                }
            }
            if (iter.hasNext()) {
                navigator.next(lexem);
            }
        }
    }

    public CommandCompiler find(@NonNull LexemSequence lexemSequence) {
        final Navigator navigator = getNavigator();
        final PushbackIterator<Lexem> iter = new PushbackIterator<>(lexemSequence.get().iterator());
        CommandCompiler compiler = null;
        Lexem lexem = null;
        while (iter.hasNext()) {
            lexem = iter.next();
            if (navigator.contains(lexem)) {
                if (!iter.hasNext()) {
                    compiler = navigator.getCompiler(lexem);
                    break;
                } else if (navigator.isTerminal(lexem)) {
                    compiler = null;
                    break;
                } else {
                    navigator.next(lexem);
                }
            } else {
                Lexem variable;
                if ((variable = findVariable(navigator)) != null) {
                    if (navigator.isTerminal(variable)) {
                        compiler = navigator.getCompiler(variable);
                        break;
                    } else {
                        navigator.next(variable);
                    }
                    if (!findFirstOccupation(navigator, iter)) {
                        compiler = null;
                        break;
                    }
                } else {
                    compiler = null;
                    break;
                }
            }
        }
        return compiler;
    }

    protected Lexem findVariable(final Navigator navigator) {
        if (navigator.contains(VAR_D)) {
            return VAR_D;
        }
        if (navigator.contains(VAR_N)) {
            return VAR_N;
        }
        if (navigator.contains(VAR_E)) {
            return VAR_E;
        }
        if (navigator.contains(VAR_NN)) {
            return VAR_NN;
        }
        if (navigator.contains(VAR_DD)) {
            return VAR_DD;
        }
        if (navigator.contains(VAR_EE)) {
            return VAR_EE;
        }
        if (navigator.contains(VAR_NNNN)) {
            return VAR_NNNN;
        }
        return null;
    }

    protected boolean findFirstOccupation(Navigator navigator, PushbackIterator<Lexem> iter) {
        while (iter.hasNext()) {
            Lexem lexem = iter.next();
            if (navigator.contains(lexem)) {
                iter.pushback(lexem);
                return true;
            }
        }
        return false;
    }
}
