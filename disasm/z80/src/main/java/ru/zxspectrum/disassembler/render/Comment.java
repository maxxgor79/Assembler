package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public class Comment extends Cell {
    private static final String PREFIX = " ;";
    @Getter
    @Setter
    private String text;

    @Override
    public String generate() throws RenderException {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        if (text != null) {
            sb.append(text);
        }
        return sb.toString();
    }
}
