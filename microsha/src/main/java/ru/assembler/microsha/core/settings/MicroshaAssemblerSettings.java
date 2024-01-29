package ru.assembler.microsha.core.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import ru.assembler.core.settings.AssemblerSettings;

/**
 * @author Maxim Gorin
 */

public class MicroshaAssemblerSettings extends AssemblerSettings {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceRkm;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceWav;

    @Override
    public void load(@NonNull CommandLine cli) {
        super.load(cli);
        if (cli.hasOption("wav")) {
            setProduceWav(true);
        }
        if (cli.hasOption("rkm")) {
            setProduceRkm(true);
        }
    }
}
