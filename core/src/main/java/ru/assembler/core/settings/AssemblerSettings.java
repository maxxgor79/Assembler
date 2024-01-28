package ru.assembler.core.settings;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class AssemblerSettings extends BaseSettings {
    public AssemblerSettings() {
    }

    public AssemblerSettings(@NonNull final CommandLine cli) {
        load(cli);
    }

    public void load(@NonNull CommandLine cli) {
        if (cli.hasOption("st")) {
            setStrictConversion(true);
        }
        if (cli.hasOption("a")) {
            setDefaultAddress(new BigInteger(cli.getOptionValue("a")));
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
        if (cli.hasOption("cpu")) {
            setCpuModels(cli.getOptionValue("cpu"));
        }
    }

    @Override
    public void load(@NonNull String name) throws IOException {
        throw new UnsupportedOperationException();
    }
}
