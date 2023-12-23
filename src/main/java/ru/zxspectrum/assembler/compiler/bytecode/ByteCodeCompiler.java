package ru.zxspectrum.assembler.compiler.bytecode;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.error.AssemblerException;
import ru.zxspectrum.assembler.error.ParserException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class ByteCodeCompiler {
    private PreTranslatedCommand preTranslatedCommand;

    public ByteCodeCompiler(@NonNull String codePattern, ByteOrder byteOrder) {
        if (codePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("codePattern is null or empty");
        }
        final CommandPatternParser parser = new CommandPatternParser(codePattern, byteOrder);
        try {
            preTranslatedCommand = new PreTranslatedCommand(parser.preTranslate(), byteOrder);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            throw new AssemblerException(e.getMessage());
        }
    }


    public byte[] compile(@NonNull BigInteger... values) {
        return preTranslatedCommand.encode(values);
    }

    public int getSize() {
        return preTranslatedCommand.getSize();
    }

    public int getArgOffset(int argIndex) {
        return preTranslatedCommand.getOffset(argIndex);
    }
}
