package ru.assembler.zxspectrum.io.tap;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.resource.Loader;
import ru.assembler.zxspectrum.basic.Lexem;
import ru.assembler.zxspectrum.basic.ParserException;
import ru.assembler.zxspectrum.basic.compile.Compiler;
import ru.assembler.zxspectrum.basic.compile.Replacer;
import ru.assembler.io.LEDataOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Maxim Gorin
 */
@Slf4j
public final class TapUtil {

    public static final String EXTENSION = "tap";

    private static final String LOADER_NAME = "loader.bas";

    private static final String PROGRAM_NAME = "program";

    private static final String DATA_NAME = "program1";

    private static final String VAR_LOAD_ADDR = "loadAddr";

    private static final String VAR_RUN_ADDR = "runAddr";

    private TapUtil() {

    }

    static void createProgram(@NonNull TapData tapData, @NonNull String resourseName,
                              int address)
            throws ParserException, IOException {
        final Compiler compiler = new Compiler();
        compiler.setInputStream(Loader.openBasic(resourseName));
        Lexem lexem = new Lexem(Integer.valueOf(address));
        compiler.setReplacer(new Replacer().add(VAR_LOAD_ADDR, lexem).add(VAR_RUN_ADDR, lexem));
        byte[] compiled = compiler.compile();

        final DataBlock dBlock = new DataBlock();
        dBlock.setContent(compiled);

        final ProgramParams programParams = new ProgramParams();
        programParams.setProgramSize(compiled.length);
        programParams.setAutostartLine(10);
        final HeaderBlock hBlock = new HeaderBlock(programParams);
        hBlock.setFilename(PROGRAM_NAME);
        hBlock.setDataBlockLength(compiled.length);
        tapData.add(hBlock);
        tapData.add(dBlock);

    }

    static void createData(@NonNull TapData tapData, @NonNull byte[] data, int address) {
        final DataBlock dBlock = new DataBlock();
        dBlock.setContent(data);
        final BytesParams bytesParams = new BytesParams();
        bytesParams.setStartAddress(address);
        final HeaderBlock hBlock = new HeaderBlock(bytesParams);
        hBlock.setFilename(DATA_NAME);
        hBlock.setDataBlockLength(data.length);
        tapData.add(hBlock);
        tapData.add(dBlock);
    }

    public static TapData createBinaryTap(@NonNull byte[] data, int address) throws IOException {
        final TapData tapData = new TapData();
        try {
            createProgram(tapData, LOADER_NAME, address);
            createData(tapData, data, address);
        } catch (ParserException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return tapData;
    }

    public static TapData createBinaryTap(@NonNull File file, @NonNull byte[] data, int address)
            throws IOException {

        final TapData tapData = createBinaryTap(data, address);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            tapData.write(new LEDataOutputStream(fos));
        }
        return tapData;
    }

    public static int calculateChecksum(byte[] data) {
        return calculateChecksum(data, 0, data.length);
    }

    public static int calculateChecksum(@NonNull byte[] data, int offset, int len) {
        if (len == 0) {
            return 0;
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if ((offset + len) > data.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int value = data[offset];
        for (int i = 1; i < len; i++) {
            value ^= data[i + offset];
        }
        return value & 0xFF;
    }
}
