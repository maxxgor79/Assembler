package ru.zxspectrum.disassembler.loader;

import lombok.NonNull;
import ru.zxspectrum.disassembler.command.Behavior;
import ru.zxspectrum.disassembler.command.CommandRecord;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * @author maxim
 * Date: 12/25/2023
 */
public class DisassemblerLoader extends CommandLoader<Set<CommandRecord>> {
    @Override
    protected void prepare(@NonNull final Set<CommandRecord> set, final int lineNumber, @NonNull final String code
            , @NonNull final String command, final Behavior behavior, final String jumpAddressPattern) {
        if (behavior == null && jumpAddressPattern == null) {
            set.add(new CommandRecord(code, command));
        } else {
            set.add(new CommandRecord(code, command, behavior, jumpAddressPattern));
        }
    }

    @Override
    public Set<CommandRecord> load(@NonNull final InputStream is, @NonNull final Charset encoding) throws IOException {
        final Set<CommandRecord> set = new HashSet<>();
        load(set, is, encoding);
        return set;
    }
}
