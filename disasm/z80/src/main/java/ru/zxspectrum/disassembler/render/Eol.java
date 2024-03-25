package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public class Eol extends Cell {
    @Getter
    @Setter
    @NonNull
    private String value = System.lineSeparator();

    @Override
    public String generate() throws RenderException {
        return value;
    }
}
