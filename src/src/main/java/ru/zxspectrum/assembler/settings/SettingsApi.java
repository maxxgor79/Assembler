package ru.zxspectrum.assembler.settings;

import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public interface SettingsApi {

    public ByteOrder getByteOrder();

    public Encoding getSourceEncoding();

    public Encoding getPlatformEncoding();

    public BigInteger getMinAddress();

    public BigInteger getMaxAddress();

}
