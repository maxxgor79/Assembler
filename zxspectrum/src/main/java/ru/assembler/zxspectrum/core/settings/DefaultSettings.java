package ru.assembler.zxspectrum.core.settings;

import lombok.NonNull;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.lang.Encoding;
import ru.assembler.core.settings.SettingsApi;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
public class DefaultSettings implements SettingsApi {
    protected static final ByteOrder BYTE_ORDER = ByteOrder.LittleEndian;

    protected static final Encoding SOURCE_ENCODING = Encoding.UTF_8;

    protected static final Encoding PLATFORM_ENCODING = Encoding.ASCII;

    protected static final BigInteger DEFAULT_ADDRESS = BigInteger.valueOf(0x8000L);

    protected static final BigInteger MIN_ADDRESS = BigInteger.ZERO;

    protected static final BigInteger MAX_ADDRESS = BigInteger.valueOf(0xffff);

    protected static final File OUTPUT_DIRECTORY = new File("output");

    protected static final String MINOR_VERSION = Integer.toString(1);

    protected static final String MAJOR_VERSION = Integer.toString(1);

    protected static final boolean STRICT_CONVERSION = false;

    protected static final String CMD_FILENAME = "asm";

    @Override
    public void load(String name) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public BigInteger getDefaultAddress() {
        return DEFAULT_ADDRESS;
    }

    @Override
    public ByteOrder getByteOrder() {
        return BYTE_ORDER;
    }

    @Override
    public Encoding getSourceEncoding() {
        return SOURCE_ENCODING;
    }

    @Override
    public Encoding getPlatformEncoding() {
        return PLATFORM_ENCODING;
    }

    @Override
    public BigInteger getMinAddress() {
        return MIN_ADDRESS;
    }

    @Override
    public BigInteger getMaxAddress() {
        return MAX_ADDRESS;
    }

    @Override
    public File getOutputDirectory() {
        return OUTPUT_DIRECTORY;
    }

    @Override
    public String getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public String getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public boolean isStrictConversion() {
        return STRICT_CONVERSION;
    }

    @Override
    public String getCmdFilename() {
        return CMD_FILENAME;
    }

    @Override
    public String getCpuModels() {
        return null;
    }

    @Override
    public void merge(@NonNull SettingsApi setting) {
        throw new UnsupportedOperationException();
    }
}
