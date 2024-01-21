package ru.assembler.zxspectrum.core.settings;

import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import ru.assembler.core.settings.AssemblerSettings;

public class Z80AssemblerSettings extends AssemblerSettings {
    @Override
    public void load(@NonNull CommandLine cli) {
        super.load(cli);
        if (cli.hasOption("tap")) {
            this.setProduceFormat(true);
        }
    }
}
