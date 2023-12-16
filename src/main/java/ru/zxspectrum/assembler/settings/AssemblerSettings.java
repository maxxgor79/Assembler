package ru.zxspectrum.assembler.settings;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@EqualsAndHashCode
@ToString
public class AssemblerSettings extends BaseSettings {
    @NonNull
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private BigInteger address;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean produceTap;

    @Getter
    @Setter(AccessLevel.PROTECTED   )
    private boolean produceWav;


    public AssemblerSettings() {

    }

    public AssemblerSettings(CommandLine cli) {
        load(cli);
    }

    public void load(@NonNull CommandLine cli) {
        if (cli.hasOption("st")) {
            setStrictConversion(true);
        }
        if (cli.hasOption("a")) {
            setAddress(new BigInteger(cli.getOptionValue("a")));
        }
        if (cli.hasOption("min")) {
            setMinAddress(new BigInteger(cli.getOptionValue("min")));
        }
        if (cli.hasOption("max")) {
            setMaxAddress(new BigInteger(cli.getOptionValue("max")));
        }
        if (cli.hasOption("o")) {
            setOutputDirectory(new File(cli.getOptionValue("o")));
        }
        if (cli.hasOption("b")) {
            setByteOrder("big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                    ByteOrder.LittleEndian);
        }
        if (cli.hasOption("s")) {
            setSourceEncoding(Encoding.valueByName(cli.getOptionValue("s")));
        }
        if (cli.hasOption("p")) {
            setPlatformEncoding(Encoding.valueByName(cli.getOptionValue("p")));
        }
        if (cli.hasOption("tap")) {
            setProduceTap(true);
        }

        if (cli.hasOption("wav")) {
            setProduceWav(true);
        }
    }

    @Override
    public void load(@NonNull String name) throws IOException {

    }
}
