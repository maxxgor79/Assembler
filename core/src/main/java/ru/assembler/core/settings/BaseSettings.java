package ru.assembler.core.settings;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.lang.Encoding;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
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

    @NonNull
    @Setter(AccessLevel.PROTECTED)
    private BigInteger defaultAddress;

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

    @Setter(AccessLevel.PROTECTED)
    private String cmdFilename;

    @Setter(AccessLevel.PROTECTED)
    private String cpuModels;

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
    public BigInteger getDefaultAddress() {return defaultAddress;}

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
    public String getCmdFilename() {
        return cmdFilename;
    }

    @Override
    public String getCpuModels() {
        return cpuModels;
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
        if (settings.getDefaultAddress() != null) {
            setDefaultAddress(settings.getDefaultAddress());
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
        if (settings.getCmdFilename() != null) {
            setCmdFilename(settings.getCmdFilename());
        }
        if (settings.getCpuModels() != null) {
            setCpuModels(settings.getCpuModels());
        }
    }
}
