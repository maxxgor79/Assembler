package ru.assembler.microsha.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.command.system.DefCommandCompiler;
import ru.assembler.core.ns.NamespaceApi;

/**
 * @author Maxim Gorin
 */

public class SetCommandCompiler extends DefCommandCompiler {
    public static final String NAME = "set";

    public SetCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi, @NonNull CompilerApi compilerApi) {
        super(name, namespaceApi, compilerApi);
    }

    public SetCommandCompiler(NamespaceApi namespaceApi, CompilerApi compilerApi) {
        super(namespaceApi, compilerApi);
    }
}
