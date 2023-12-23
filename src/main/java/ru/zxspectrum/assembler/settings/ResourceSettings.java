package ru.zxspectrum.assembler.settings;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.resource.Loader;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@EqualsAndHashCode
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
        setByteOrder("big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian);
        setMinAddress(Variables.getBigInteger(Variables.MIN_ADDRESS, BigInteger.ZERO));
        setMaxAddress(Variables.getBigInteger(Variables.MAX_ADDRESS, new BigInteger("ffff", 16)));
        setOutputDirectory(new File(Variables.getString(Variables.OUTPUT_DIRECTORY, "output")));
        setMajorVersion(Variables.getString(Variables.MAJOR_VERSION, "1"));
        setMinorVersion(Variables.getString(Variables.MINOR_VERSION, "0"));
        setCmdFilename(Variables.getString(Variables.CMD_FILENAME, "asm"));
    }
}
