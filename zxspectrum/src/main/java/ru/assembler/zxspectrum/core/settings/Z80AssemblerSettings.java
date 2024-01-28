package ru.assembler.zxspectrum.core.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import ru.assembler.core.settings.AssemblerSettings;

public class Z80AssemblerSettings extends AssemblerSettings {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceWav;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceTap;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceTzx;


    @Override
    public void load(@NonNull CommandLine cli) {
        super.load(cli);
        if (cli.hasOption("wav")) {
            this.setProduceWav(true);
        }
        if (cli.hasOption("tap")) {
            this.setProduceTap(true);
        }

        if (cli.hasOption("tzx")) {
            this.setProduceTzx(true);
        }
    }
}
