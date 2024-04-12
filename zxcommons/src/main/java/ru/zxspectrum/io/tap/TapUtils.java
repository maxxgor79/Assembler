package ru.zxspectrum.io.tap;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.io.LEDataOutputStream;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.ParserException;
import ru.zxspectrum.basic.compile.Compiler;
import ru.zxspectrum.basic.compile.Replacer;
import ru.zxspectrum.io.tzx.TzxData;
import ru.zxspectrum.util.Content;
import ru.zxspectrum.util.ContentType;

import java.io.*;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxim Gorin
 */
@Slf4j
public final class TapUtils {

    public static final String EXTENSION = "tap";

    private static final String LOADER_PATH = "asm/basic/loader.bas";

    private static final String PROGRAM_NAME = "program";

    private static final String DATA_NAME = "program1";

    private static final String VAR_LOAD_ADDR = "loadAddr";

    private static final String VAR_RUN_ADDR = "runAddr";

    private TapUtils() {

    }

    static InputStream openBasic(@NonNull String resourceName) {
        if (resourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        return TapUtils.class.getResourceAsStream("/" + resourceName);
    }

    static void createProgram(@NonNull TapData tapData, @NonNull String resourcePath,
                              int address)
            throws ParserException, IOException {
        final Compiler compiler = new Compiler();
        compiler.setInputStream(openBasic(resourcePath));
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
            createProgram(tapData, LOADER_PATH, address);
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

    public static void toBytes(@NonNull TapData tapData, @NonNull OutputStream os) throws IOException {
        for (Block block : tapData.getBlockList()) {
            block.export(os);
        }
    }

    public static void createTap(@NonNull final TzxData tzxData, @NonNull final OutputStream os) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static Collection<Content> getContent(@NonNull File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return getContent(is);
        }
    }

    public static Collection<Content> getContent(@NonNull InputStream is) throws IOException {
        final TapData tapData = new TapData();
        tapData.read(is);
        final Collection<Block> blocks = tapData.getBlocks();
        final Iterator<Block> iterator = blocks.iterator();
        final List<Content> contentList = new LinkedList<>();
        while (iterator.hasNext()) {
            final Block block = iterator.next();
            switch (block.getFlag()) {
                case Header:
                    if (((HeaderBlock) block).getHeaderType() == HeaderType.Program) {
                        if (iterator.hasNext()) {
                            final Block body = iterator.next();
                            if (body.getFlag() == Flag.Data) {
                                final byte[] content = ((Container) body).getContent();
                                final int startAddress = ((HeaderBlock) block).getProgramParams().getAutostartLine();
                                contentList.add(new Content(ContentType.Basic, BigInteger.valueOf(startAddress)
                                        , content));
                                log.info("Received program, address={}, length={}", startAddress, content.length);
                            }
                        }
                    } else if (((HeaderBlock) block).getHeaderType() == HeaderType.Code) {
                        if (iterator.hasNext()) {
                            final Block body = iterator.next();
                            if (body.getFlag() == Flag.Data) {
                                final byte[] content = ((Container) body).getContent();
                                final int startAddress = ((HeaderBlock) block).getBytesParams().getStartAddress();
                                contentList.add(new Content(ContentType.Data, BigInteger.valueOf(startAddress), content));
                                log.info("Received code, address={}, length={}", startAddress, content.length);
                            }
                        }
                    } else if (((HeaderBlock) block).getHeaderType() == HeaderType.Bytes) {
                        if (iterator.hasNext()) {
                            final Block body = iterator.next();
                            if (body.getFlag() == Flag.Data) {
                                final byte[] content = ((Container) body).getContent();
                                final int startAddress = ((HeaderBlock) block).getBytesParams().getStartAddress();
                                ContentType contentType = ContentType.Data;
                                if (startAddress == 16384 && content.length == 6192) {
                                    contentType = ContentType.Screen;
                                }
                                contentList.add(new Content(contentType, BigInteger.valueOf(startAddress), content));
                                log.info("Received bytes, address={}, length={}", startAddress, content.length);
                            }
                        }
                    }
                    break;
                case Data:
                    final byte[] content = ((Container) block).getContent();
                    contentList.add(new Content(ContentType.Data, null, content));
                    log.info("Received code or bytes, length={}", content.length);
                    break;
                case Unknown:
                    throw new UnsupportedOperationException();
            }
        }
        return contentList;
    }
}
