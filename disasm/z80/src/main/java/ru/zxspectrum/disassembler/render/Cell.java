package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.Setter;

/**
 * @author maxim
 * Date: 12/24/2023
 */
public abstract class Cell implements Render {
    @Getter
    @Setter
    protected static boolean uppercase = true;

    protected static final String SPACE = " ";
}
