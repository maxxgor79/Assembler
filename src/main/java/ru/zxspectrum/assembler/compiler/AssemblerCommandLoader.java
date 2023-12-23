package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class AssemblerCommandLoader extends CommandLoader<MultiValuedMap<String, LexemSequence>> {
    public AssemblerCommandLoader() {
    }

    @Override
    protected void prepare(MultiValuedMap<String, LexemSequence> map, int lineNumber, String codePattern, String command) {
        map.put(codePattern, new LexemSequence(command));
    }

    @Override
    public MultiValuedMap<String, LexemSequence> load(@NonNull InputStream is, Charset encoding) throws IOException {
        MultiValuedMap<String, LexemSequence> map = new ArrayListValuedHashMap<>();
        load(map, is, encoding);
        return map;
    }
}
