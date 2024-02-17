package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.text.Output;
import ru.assembler.core.ns.NamespaceApi;

import java.math.BigInteger;

public class PrintlnCommandCompiler extends PrintCommandCompiler {
    protected static final String[] NAMES = {"println"};

    public PrintlnCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull CompilerApi compilerApi) {
        super(namespaceApi, compilerApi);
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    protected void print(String s) {
        Output.println(s);
    }

    @Override
    protected void print(BigInteger n) {
        Output.println(n.toString());
    }
}
