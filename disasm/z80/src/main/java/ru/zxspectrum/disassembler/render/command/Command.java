package ru.zxspectrum.disassembler.render.command;

import lombok.Getter;
import lombok.Setter;
import ru.zxspectrum.disassembler.render.Cell;

/**
 * @author maxim
 * Date: 1/5/2024
 */
public abstract class Command extends Cell {
    @Getter
    @Setter
    protected static boolean uppercase = true;
}
