package ru.zxspectrum.assembler.settings;

import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
public interface SettingsApi {

    void load(String name) throws IOException;

    ByteOrder getByteOrder();

    Encoding getSourceEncoding();

    Encoding getPlatformEncoding();

    BigInteger getMinAddress();

    BigInteger getMaxAddress();

    File getOutputDirectory();

    String getMajorVersion();

    String getMinorVersion();

    boolean isStrictConversion();

    String getCmdFilename();

    String getCpuModels();

    void merge(SettingsApi setting);
}
