package ru.zxspectrum.disassembler.render.system;

import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.render.command.Command;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public class Z80 extends Command {
    protected static final String NAME = ".Z80";

    @Override
    public String generate() throws RenderException {
        return toUpperOrLowerCase(NAME);
    }
}
