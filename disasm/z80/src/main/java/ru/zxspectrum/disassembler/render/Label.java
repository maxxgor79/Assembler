package ru.zxspectrum.disassembler.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/3/2024
 */

@EqualsAndHashCode
public class Label extends Cell {
    protected static final String LABEL_PREFIX = "lbl";

    protected static int labelCounter = 1;

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
    public static String generateLabelName(int addressSize) {
        String name =  String.format("%s%0" + addressSize * 2 + "X", LABEL_PREFIX, labelCounter++);
        return toUpperOrLowerCase(name);
    }
}
