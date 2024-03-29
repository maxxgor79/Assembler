package ru.zxspectrum.disassembler.settings;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.decode.DecoderStrategy;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.utils.NumberStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author Maxim Gorin
 */
public class BaseSettings implements Settings {
    @Setter
    @Getter
    @NonNull
    protected ByteOrder byteOrder;

    @Setter
    @Getter
    protected int addressDimension;

    @Setter
    @Getter
    @NonNull
    private String minorVersion;

    @Setter
    @Getter
    @NonNull
    private String majorVersion;

    @Setter
    @Getter
    @NonNull
    private String destEncoding;

    @Setter
    @Getter
    @NonNull
    private BigInteger defaultAddress;

    @Setter
    @Getter
    @NonNull
    private BigInteger minAddress;

    @Setter
    @Getter
    @NonNull
    private BigInteger maxAddress;

    @Setter
    @Getter
    @NonNull
    private String commentsTemplate;

    @Setter
    @Getter
    @NonNull
    private String outputDirectory;

    @Setter
    @Getter
    private boolean addressVisible;

    private final Collection<String> templates = new LinkedList<>();

    @Setter
    @Getter
    @NonNull
    private String cmdFilename;

    @Setter
    @Getter
    private boolean stdout;

    @Setter
    @Getter
    private int radix;

    @Setter
    @Getter
    private boolean upperCase;

    @Setter
    @Getter
    @NonNull
    private NumberStyle numberStyle;

    @Setter
    @Getter
    @NonNull
    private DecoderStrategy strategy;

    @Setter
    @Getter
    @NonNull
    private boolean hasComments;

    @Setter
    @Getter
    @NonNull
    private String encoding;

    public BaseSettings() {

    }

    @Override
    public void load(@NonNull InputStream is) throws IOException {
        Variables.load(is);
        majorVersion = Variables.getString(Variables.MAJOR_VERSION, majorVersion);
        minorVersion = Variables.getString(Variables.MINOR_VERSION, minorVersion);
        destEncoding = Variables.getString(Variables.DEST_ENCODING, destEncoding);
        String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
        byteOrder = "big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian;
        defaultAddress = Variables.getBigInteger(Variables.DEFAULT_ADDRESS, defaultAddress);
        minAddress = Variables.getBigInteger(Variables.MIN_ADDRESS, minAddress);
        maxAddress = Variables.getBigInteger(Variables.MAX_ADDRESS, maxAddress);
        commentsTemplate = Variables.getString(Variables.COMMENT_TEMPLATE, commentsTemplate);
        addressDimension = Variables.getInt(Variables.ADDRESS_DIMENSION, addressDimension);
        cmdFilename = Variables.getString(Variables.CMD_FILENAME, "disasm");
        for (int i = 0; i < 256; i++) {
            value = Variables.getString(Variables.TEMPLATE + i, null);
            if (value == null) {
                break;
            }
            templates.add(value);
        }
        outputDirectory = Variables.getString(Variables.OUTPUT_DIRECTORY, "output");
    }

    @Override
    public void merge(@NonNull Settings settings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public int getAddressDimension() {
        return addressDimension;
    }

    @Override
    public String getMinorVersion() {
        return minorVersion;
    }

    @Override
    public String getMajorVersion() {
        return majorVersion;
    }

    @Override
    public String getDestEncoding() {
        return destEncoding;
    }

    @Override
    public BigInteger getDefaultAddress() {
        return defaultAddress;
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
    public String getCommentsTemplate() {
        return commentsTemplate;
    }

    @Override
    public Collection<String> getTemplates() {
        return Collections.unmodifiableCollection(templates);
    }

    @Override
    public File getOutputDirectory() {
        return new File(outputDirectory);
    }

    @Override
    public boolean isAddressVisible() {
        return addressVisible;
    }

    @Override
    public String getCmdFilename() {
        return cmdFilename;
    }

    @Override
    public boolean getStdout() {
        return stdout;
    }

    @Override
    public int getRadix() {
        return radix;
    }

    @Override
    public boolean isUpperCase() {
        return upperCase;
    }

    @Override
    public DecoderStrategy getStrategy() {
        return strategy;
    }

    @Override
    public boolean hasComments() {
        return this.hasComments;
    }

    protected void setTemplates(@NonNull Collection<String> templates) {
        this.templates.addAll(templates);
    }
}
