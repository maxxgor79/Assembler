package ru.assembler.microsha.core.settings;

import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import ru.assembler.core.settings.AssemblerSettings;

public class MicroshaAssemblerSettings extends AssemblerSettings {
    @Override
    public void load(@NonNull CommandLine cli) {
        super.load(cli);
        if (cli.hasOption("rkm")) {
            setProduceFormat(true);
        }
    }
}
