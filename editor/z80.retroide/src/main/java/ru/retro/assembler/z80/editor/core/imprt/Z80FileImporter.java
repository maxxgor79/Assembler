package ru.retro.assembler.z80.editor.core.imprt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import ru.retro.assembler.editor.core.imprt.FileImporter;
import ru.retro.assembler.editor.core.imprt.SourceDescriptor;
import ru.retro.assembler.editor.core.sys.CallException;
import ru.retro.assembler.editor.core.sys.Caller;
import ru.retro.assembler.editor.core.ui.address.AddressDialog;
import ru.retro.assembler.editor.core.util.FileUtils;
import ru.retro.assembler.editor.core.util.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Maxim Gorin Date: 04.04.2024
 */
@Slf4j
public class Z80FileImporter implements FileImporter {
    protected static final String TAP_EXTENSION = "tap";

    protected static final String BIN_EXTENSION = "bin";

    protected static final String ASM_EXTENSION = "asm";

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
        if (isBin(file)) {
            return importBinFile(file, encoding);
        } else if (isTap(file)) {
            return importTapFile(file, encoding);
        } else {
            throw new IOException("File <" + file.getAbsolutePath() + "> is not acceptable");
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
        final PrintStream stdout = System.out;
        final PrintStream stderr = System.err;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            final ByteArrayOutputStream err = new ByteArrayOutputStream();
            System.setErr(new PrintStream(err));
            Caller.call("ru.zxspectrum.disassembler.Disassembler", getArguments(file, address, encoding));
            final StringBuilder sb = new StringBuilder();
            sb.append(new String(out.toByteArray(), encoding)).append(new String(err.toByteArray(), encoding));
            final SourceDescriptor sd = new SourceDescriptor();
            sd.setText(sb.toString());
            sd.setFileName(FileUtils.addExt(FilenameUtils.removeExtension(file.getAbsolutePath()), ASM_EXTENSION));
            return Arrays.asList(sd);
        } catch (CallException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        } finally {
            System.setOut(stdout);
            System.setErr(stderr);
        }
    }

    private Collection<SourceDescriptor> importTapFile(final File file, final String encoding) throws IOException {
        final AddressDialog dialog = getAddressDialog();
        dialog.setLocationRelativeTo(null);
        TapUtil
    }
}
