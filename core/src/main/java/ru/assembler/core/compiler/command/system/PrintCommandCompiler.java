package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.Output;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;

import java.math.BigInteger;
import java.util.Iterator;

public class PrintCommandCompiler implements CommandCompiler {
    protected static final String[] NAMES = {"print", "message", "echo"};

    @Override
    public String[] names() {
        return NAMES;
    }

    protected final NamespaceApi namespaceApi;

    protected final CompilerApi compilerApi;

    public PrintCommandCompiler(@NonNull final NamespaceApi namespaceApi, @NonNull final CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(@NonNull final LexemSequence lexemSequence) {
        Lexem nextLexem;
        final Iterator<Lexem> iterator = lexemSequence.get().iterator();
        if (!iterator.hasNext() || !contains(names(), iterator.next().getValue())) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem == null) {
            throw new CompilerException(compilerApi.getFd(), compilerApi.getLineNumber(), Messages
                    .getMessage(Messages.FILE_PATH_EXCEPTED));
        }
        while (true) {
            if (nextLexem.getType() == LexemType.STRING) {
                print(nextLexem.getValue());
            } else {
                final Expression expression = new Expression(compilerApi.getFd(), iterator, namespaceApi);
                final Expression.Result result = expression.evaluate(nextLexem);
                if (result.isUndefined()) {
                    throw new CompilerException(nextLexem.getFd(), nextLexem.getLineNumber()
                            , Messages.getMessage(Messages.CONSTANT_VALUE_REQUIRED));
                }
                print(result.getValue());
            }
            nextLexem = iterator.hasNext() ? iterator.next() : null;
            if (nextLexem == null) {
                break;
            }
        }
        return new byte[0];
    }

    protected void print(String s) {
        Output.print(s);
    }

    protected void print(BigInteger n) {
        Output.print(n.toString());
    }
}
