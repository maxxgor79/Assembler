package ru.zxspectrum.assembler.settings;

import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
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

    void merge(SettingsApi setting);
}
