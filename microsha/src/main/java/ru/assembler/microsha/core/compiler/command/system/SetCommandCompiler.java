package ru.assembler.microsha.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.command.system.DefCommandCompiler;
import ru.assembler.core.ns.NamespaceApi;

/**
 * @author Maxim Gorin
 */

public class SetCommandCompiler extends DefCommandCompiler {
    protected static final String[] NAMES = {"set"};

    public SetCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull CompilerApi compilerApi) {
        super(namespaceApi, compilerApi);
    }

    @Override
    public String[] names() {
        return NAMES;
    }
}
