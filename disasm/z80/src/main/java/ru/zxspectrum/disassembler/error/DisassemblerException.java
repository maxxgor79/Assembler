package ru.zxspectrum.disassembler.error;

import ru.zxspectrum.disassembler.io.ErrorOutput;
import ru.zxspectrum.disassembler.io.Output;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class DisassemblerException extends RuntimeException {

    public DisassemblerException() {
    }

    public DisassemblerException(String message) {
        super(message);
    }

    public DisassemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisassemblerException(Throwable cause) {
        super(cause);
    }

    public DisassemblerException(int pos, String message, Object ... args) {
        this(ErrorOutput.formattedError(pos, message, args));
    }

    public DisassemblerException(File file, int pos, String message, Object ... args) {
        this(ErrorOutput.formattedError(file.getAbsolutePath(), pos, message, args));
    }
}
