package ru.zxspectrum.disassembler.settings;

import ru.zxspectrum.disassembler.decode.DecoderStrategy;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.utils.NumberStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;

/**
 * @author Maxim Gorin
 * Date: 28.02.2023
 */
public interface Settings {
    ByteOrder getByteOrder();

    int getAddressDimension();

    String getMinorVersion();

    String getMajorVersion();

    String getDestEncoding();

    BigInteger getDefaultAddress();

    BigInteger getMinAddress();

    BigInteger getMaxAddress();

    String getCommentsTemplate();

    Collection<String> getTemplates();

    File getOutputDirectory();

    boolean isAddressVisible();

    String getCmdFilename();

    boolean getStdout();

    int getRadix();

    boolean isUpperCase();

    NumberStyle getNumberStyle();

    DecoderStrategy getStrategy();

    boolean hasComments();

    String getEncoding();

    void load(InputStream is) throws IOException;

    void merge(Settings settings);
}
