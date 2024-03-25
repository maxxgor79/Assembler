package ru.zxspectrum.disassembler.render;

import ru.zxspectrum.disassembler.error.RenderException;

public interface Render {
    String generate() throws RenderException;
}
