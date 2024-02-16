package ru.assembler.core.settings;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.lang.Encoding;
import ru.assembler.core.resource.Loader;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class ResourceSettings extends BaseSettings {
    @Override
    public void load(@NonNull String name) throws IOException {
        Variables.load(Loader.openRoot("settings.properties"));
        setPlatformEncoding(Encoding.valueByName(
                Variables.getString(Variables.PLATFORM_ENCODING, Encoding
                        .ASCII.getName())));
        setSourceEncoding(
                Encoding.valueByName(Variables.getString(Variables.SOURCE_ENCODING, Encoding
                        .UTF_8.getName())));
        final String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
        setDefaultAddress(Variables.getBigInteger(Variables.DEFAULT_ADDRESS, BigInteger.valueOf(0x8000)));
        setByteOrder("big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian);
        setMinAddress(Variables.getBigInteger(Variables.MIN_ADDRESS, BigInteger.ZERO));
        setMaxAddress(Variables.getBigInteger(Variables.MAX_ADDRESS, BigInteger.valueOf(0xffff)));
        setOutputDirectory(new File(Variables.getString(Variables.OUTPUT_DIRECTORY, "output")));
        setMajorVersion(Variables.getString(Variables.MAJOR_VERSION, "1"));
        setMinorVersion(Variables.getString(Variables.MINOR_VERSION, "0"));
        setCmdFilename(Variables.getString(Variables.CMD_FILENAME, "asm"));
        setCpuModels(Variables.getString(Variables.CPU_MODELS));
    }
}
