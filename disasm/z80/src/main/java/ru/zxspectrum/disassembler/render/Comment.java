package ru.zxspectrum.disassembler.render;

import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.sys.Environment;
import ru.zxspectrum.disassembler.utils.ConverterUtils;
import ru.zxspectrum.disassembler.utils.NumberStyle;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public class Comment extends Cell {
    private static final String PREFIX = "\t;";

    @Getter
    @Setter
    @NonNull
    private BigInteger address;

    @Getter
    @Setter
    @NonNull
    private byte [] byteCode;

    @Getter
    @Setter
    private String text;

    @Getter
    private int tabCount = 1;

    private Environment env;

    public Comment(@NonNull Environment env) {
        this.env = env;
    }

    public void setTabCount(int tabCount) {
        if (tabCount < 0) {
            throw new IllegalArgumentException("tabCount < 0");
        }
        this.tabCount = tabCount;
    }

    @Override
    public String generate() throws RenderException {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        if (address != null) {
            sb.append(renderAddress());
        }
        if (byteCode != null) {
            sb.append(' ');
            sb.append(renderByteCode());
        }
        if (text != null) {
            sb.append(StringUtils.repeat('\t', tabCount));
            sb.append(text);
        }
        return sb.toString();
    }

    protected String renderAddress() {
        if (address == null) {
            return null;
        }
        final String hex = String.format("%0" + env.getAddressDimension() + "X", address.intValue());
        return hex;
    }

    protected String renderByteCode() {
        if (byteCode == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteCode.length; i++) {
            final String hex = String.format("%02X", byteCode[i] & 0xff);
            sb.append(hex);
        }
        return sb.toString();
    }
}
