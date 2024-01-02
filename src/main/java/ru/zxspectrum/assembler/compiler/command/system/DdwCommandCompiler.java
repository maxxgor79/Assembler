package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.lang.Type;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.settings.SettingsApi;

public class DdwCommandCompiler extends DwCommandCompiler {
    public static final String NAME = "ddw";

    public static final String ALT_NAME = "defdw";

    public DdwCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi) {
        super(name, namespaceApi, settingsApi, compilerApi);
    }

    @Override
    protected Type getDestType() {
        return Type.UInt32;
    }
}
