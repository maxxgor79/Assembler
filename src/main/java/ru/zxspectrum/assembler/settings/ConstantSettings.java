package ru.zxspectrum.assembler.settings;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@EqualsAndHashCode
@ToString
public class ConstantSettings implements SettingsApi {
    private File outputFile = new File("output");

    @Override
    public void load(@NonNull String name) throws IOException {

    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.LittleEndian;
    }

    @Override
    public Encoding getSourceEncoding() {
        return Encoding.UTF_8;
    }

    @Override
    public Encoding getPlatformEncoding() {
        return Encoding.ASCII;
    }

    @Override
    public BigInteger getMinAddress() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getMaxAddress() {
        return new BigInteger("ffff", 16);
    }

    @Override
    public File getOutputDirectory() {
        return outputFile;
    }

    @Override
    public String getMajorVersion() {
        return "1";
    }

    @Override
    public String getMinorVersion() {
        return "0";
    }

    @Override
    public boolean isStrictConversion() {
        return false;
    }

    @Override
    public void merge(@NonNull SettingsApi setting) {
        throw new UnsupportedOperationException();
    }
}
