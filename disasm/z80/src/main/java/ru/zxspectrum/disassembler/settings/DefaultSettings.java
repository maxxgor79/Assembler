package ru.zxspectrum.disassembler.settings;

import lombok.NonNull;
import ru.zxspectrum.disassembler.decode.DecoderStrategy;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.utils.NumberStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Maxim Gorin
 */
public class DefaultSettings implements Settings {
    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.LittleEndian;
    }

    @Override
    public int getAddressDimension() {
        return 4;
    }

    @Override
    public String getMinorVersion() {
        return "1";
    }

    @Override
    public String getMajorVersion() {
        return "1";
    }

    @Override
    public String getDestEncoding() {
        return Charset.defaultCharset().name();
    }

    @Override
    public BigInteger getDefaultAddress() {
        return new BigInteger("8000", 16);
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
    public String getCommentsTemplate() {
        return "";
    }

    @Override
    public Collection<String> getTemplates() {
        return Collections.emptyList();
    }

    @Override
    public File getOutputDirectory() {
        return new File("output");
    }

    @Override
    public boolean isAddressVisible() {
        return false;
    }

    @Override
    public String getCmdFilename() {
        return "disasm";
    }

    @Override
    public boolean getStdout() {
        return false;
    }

    @Override
    public int getRadix() {
        return 16;
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

    @Override
    public NumberStyle getNumberStyle() {
        return NumberStyle.Classic;
    }

    @Override
    public DecoderStrategy getStrategy() {
        return DecoderStrategy.Sequentially;
    }

    @Override
    public boolean hasComments() {
        return true;
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public void load(@NonNull InputStream is) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(@NonNull Settings settings) {
        throw new UnsupportedOperationException();
    }
}
