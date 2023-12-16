package ru.zxspectrum.assembler.settings;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public abstract class BaseSettings implements SettingsApi {
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private Encoding platformEncoding;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private Encoding sourceEncoding;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private ByteOrder byteOrder;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private BigInteger minAddress;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private BigInteger maxAddress;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private File outputDirectory;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String minorVersion;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String majorVersion;

    @Setter(AccessLevel.PROTECTED)
    private boolean strictConversion;

    BaseSettings() {

    }

    @Override
    public void load(@NonNull String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public Encoding getSourceEncoding() {
        return sourceEncoding;
    }

    @Override
    public Encoding getPlatformEncoding() {
        return platformEncoding;
    }

    @Override
    public BigInteger getMinAddress() {
        return minAddress;
    }

    @Override
    public BigInteger getMaxAddress() {
        return maxAddress;
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public String getMajorVersion() {
        return majorVersion;
    }

    @Override
    public String getMinorVersion() {
        return minorVersion;
    }

    @Override
    public boolean isStrictConversion() {
        return strictConversion;
    }

    @Override
    public void merge(@NonNull SettingsApi settings) {
        if (settings.getPlatformEncoding() != null) {
            setPlatformEncoding(settings.getPlatformEncoding());
        }
        if (settings.getSourceEncoding() != null) {
            setSourceEncoding(settings.getSourceEncoding());
        }
        if (settings.getByteOrder() != null) {
            setByteOrder(settings.getByteOrder());
        }
        if (settings.getMinAddress() != null) {
            setMinAddress(settings.getMinAddress());
        }
        if (settings.getMaxAddress() != null) {
            setMaxAddress(settings.getMaxAddress());
        }
        if (settings.getOutputDirectory() != null) {
            setOutputDirectory(settings.getOutputDirectory());
        }
        if (settings.getMajorVersion() != null) {
            setMajorVersion(settings.getMajorVersion());
        }
        if (settings.getMinorVersion() != null) {
            setMinorVersion(settings.getMinorVersion());
        }
        setStrictConversion(settings.isStrictConversion());
    }
}
