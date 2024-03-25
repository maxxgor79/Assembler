package ru.zxspectrum.disassembler.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

/**
 * @author maxim
 * Date: 1/3/2024
 */

@EqualsAndHashCode
public class Label extends Cell {
    @Getter
    @Setter
    @NonNull
    private String name;

    public Label(String name) {
        setName(name);
    }

    @Override
    public String generate() throws RenderException {
        StringBuilder sb = new StringBuilder();
        sb.append(SPACE).append(name).append(":").append(System.lineSeparator());
        return sb.toString();
    }
}
