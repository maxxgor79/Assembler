package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.zxspectrum.disassembler.error.RenderException;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public class Tab extends Cell {
    @Getter
    private int length = 2;

    @Getter
    private char delimiter = '\t';

    private String space;

    public Tab() {
        setDelimiter(delimiter);
    }

    public Tab(char delimiter, int length) {
        setDelimiter(delimiter);
        setLength(length);
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
        space = StringUtils.repeat(delimiter, length);
    }

    public void setLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length is negative");
        }
        space = StringUtils.repeat(delimiter, length);
    }

    @Override
    public String generate() throws RenderException {
        return space;
    }
}
