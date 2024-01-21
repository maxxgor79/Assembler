package ru.assembler.core.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.SymbolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class AssemblerCommandLoader extends CommandLoader<MultiValuedMap<String, LexemSequence>> {
    private final SettingsApi settingsApi;

    private Set<String> includedCpuModels;

    public AssemblerCommandLoader() {
        this.settingsApi = null;
    }

    public AssemblerCommandLoader(@NonNull SettingsApi settingsApi) {
        this.settingsApi = settingsApi;
        parseCpuModels(settingsApi.getCpuModels());
    }

    private void parseCpuModels(String cpuModels) {
        this.includedCpuModels = SymbolUtil.parseEnumeration(cpuModels, ",");
    }

    @Override
    protected void prepare(MultiValuedMap<String, LexemSequence> map, int lineNumber, String codePattern
            , String command, Set<String> cpuModels) {
        if (!cpuModels.isEmpty()) {
            for (String model : cpuModels) {
                if (this.includedCpuModels.contains(model)) {
                    map.put(codePattern, new LexemSequence(command));//add for special cpu
                    break;
                }
            }
        } else {
            map.put(codePattern, new LexemSequence(command));//add fort default cpu
        }
    }

    @Override
    public MultiValuedMap<String, LexemSequence> load(@NonNull InputStream is, Charset encoding) throws IOException {
        MultiValuedMap<String, LexemSequence> map = new ArrayListValuedHashMap<>();
        load(map, is, encoding);
        return map;
    }
}
