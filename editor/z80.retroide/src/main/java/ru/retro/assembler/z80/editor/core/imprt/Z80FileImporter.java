package ru.retro.assembler.z80.editor.core.imprt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.imprt.FileImporter;
import ru.retro.assembler.editor.core.imprt.SourceDescriptor;
import ru.retro.assembler.editor.core.sys.CallException;
import ru.retro.assembler.editor.core.sys.Caller;
import ru.retro.assembler.editor.core.ui.address.AddressDialog;
import ru.retro.assembler.editor.core.util.FileUtils;
import ru.zxspectrum.io.tap.TapUtils;
import ru.zxspectrum.util.Content;
import ru.zxspectrum.util.ContentType;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Maxim Gorin Date: 04.04.2024
 */
@Slf4j
public class Z80FileImporter implements FileImporter {
    protected static final String TAP_EXTENSION = "tap";

    protected static final String BIN_EXTENSION = "bin";

    protected static final String ASM_EXTENSION = "asm";

    protected static final String DISASM_CLASS = "ru.zxspectrum.disassembler.Disassembler";

    protected static final int MAX_ADDRESS = 65535;

    private static BigInteger address = BigInteger.valueOf(32768);

    private AddressDialog dialog;

    protected AddressDialog getAddressDialog() {
        if (dialog == null) {
            dialog = new AddressDialog();
        }
        return dialog;
    }

    private static boolean isTap(File file) {
        return file.getAbsolutePath().toLowerCase().endsWith(TAP_EXTENSION);
    }

    private static boolean isBin(File file) {
        return file.getAbsolutePath().toLowerCase().endsWith(BIN_EXTENSION);
    }

    private static boolean hasNoExtension(File file) {
        return "".equals(FilenameUtils.getExtension(file.getAbsolutePath()));
    }

    @Override
    public boolean isAcceptable(File file) {
        if (file == null) {
            return false;
        }
        final String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();
        return ext == "" || TAP_EXTENSION.equals(ext) | BIN_EXTENSION.equals(ext);
    }

    @Override
    public Collection<SourceDescriptor> importFile(@NonNull File file) throws IOException {
        return importFile(file, StandardCharsets.UTF_8.name());
    }

    private Collection<String> getArguments(File file, BigInteger address, String encoding) {
        return Arrays.asList("-stdout", "-lc", "lower", "-e", encoding, "-a", address.toString()
                , file.getAbsolutePath());
    }

    @Override
    public Collection<SourceDescriptor> importFile(@NonNull final File file, @NonNull final String encoding)
            throws IOException, CharacterCodingException {
        if (isBin(file) || hasNoExtension(file)) {
            return importBinFile(file, encoding);
        } else if (isTap(file)) {
            return importTapFile(file, encoding);
        } else {
            throw new IOException("File <" + file.getAbsolutePath() + "> is not acceptable");
        }
    }

    private SourceDescriptor importFile(final File file, final String encoding, BigInteger address) throws IOException,
            CharacterCodingException {
        final PrintStream stdout = System.out;
        final PrintStream stderr = System.err;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            final ByteArrayOutputStream err = new ByteArrayOutputStream();
            System.setErr(new PrintStream(err));
            Caller.call(DISASM_CLASS, getArguments(file, address, encoding));
            final StringBuilder sb = new StringBuilder();
            sb.append(new String(out.toByteArray(), encoding)).append(new String(err.toByteArray(), encoding));
            final SourceDescriptor sd = new SourceDescriptor();
            sd.setText(sb.toString());
            sd.setFileName(FileUtils.addExt(FilenameUtils.removeExtension(file.getAbsolutePath()), ""));
            return sd;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        } finally {
            System.setOut(stdout);
            System.setErr(stderr);
        }
    }

    private Collection<SourceDescriptor> importBinFile(final File file, final String encoding) throws IOException
            , CharacterCodingException {
        final AddressDialog dialog = getAddressDialog();
        dialog.setLocationRelativeTo(null);
        if (dialog.getAddress() == null) {
            dialog.setAddress(address);
        }
        if (dialog.showModal() == AddressDialog.OK) {
            address = dialog.getAddress() == null ? address : dialog.getAddress();
        } else {
            return null;
        }
        return Arrays.asList(importFile(file, encoding, address));
    }

    private Collection<SourceDescriptor> importTapFile(final File file, final String encoding) throws IOException {
        final AddressDialog dialog = getAddressDialog();
        dialog.setLocationRelativeTo(null);
        final Collection<Content> content = TapUtils.getContent(file);
        final List<SourceDescriptor> result = new LinkedList<>();
        int i = 1;
        for (Content c : content) {
            if (c.getContentType() == ContentType.Data) {
                final byte[] data = c.getBytes();
                BigInteger address = c.getStartAddress();
                if (address == null) {
                    address = BigInteger.valueOf(MAX_ADDRESS - data.length);
                }
                final File tmpFile = FileUtils.createTempFile(FilenameUtils.removeExtension(file
                        .getAbsolutePath()) + i++ + "." + ASM_EXTENSION);
                try (OutputStream is = new FileOutputStream(tmpFile)) {
                    is.write(data);
                    is.flush();
                    IOUtils.closeQuietly(is);
                }
                result.add(importFile(tmpFile, encoding, address));
            } else {
                log.info("Skip {}", c.getContentType());
            }
        }
        return result;
    }
}
