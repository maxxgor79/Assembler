package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.lang.Type;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;

public class DdwCommandCompiler extends DwCommandCompiler {
    public static final String NAME = "ddw";

    public static final String ALT_NAME = "defdw";

    public DdwCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi) {
        super(name, namespaceApi, settingsApi, compilerApi);
    }

    public DdwCommandCompiler(NamespaceApi namespaceApi, SettingsApi settingsApi, CompilerApi compilerApi) {
        super(NAME, namespaceApi, settingsApi, compilerApi);
    }

    @Override
    protected Type getDestType() {
        return Type.UInt32;
    }
}
