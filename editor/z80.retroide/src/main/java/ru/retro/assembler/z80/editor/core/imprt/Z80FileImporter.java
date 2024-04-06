package ru.retro.assembler.z80.editor.core.imprt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.imprt.FileImporter;
import ru.retro.assembler.editor.core.sys.CallException;
import ru.retro.assembler.editor.core.sys.Caller;
import ru.retro.assembler.editor.core.ui.address.AddressDialog;

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
  private AddressDialog dialog;

  protected AddressDialog getAddressDialog() {
    if (dialog == null) {
      dialog = new AddressDialog();
    }
    return dialog;
  }

  @Override
  public boolean isAcceptable(File file) {
    if (file == null) {
      return false;
    }
    final String ext = FilenameUtils.getExtension(file.getAbsolutePath());
    return "bin".equalsIgnoreCase(ext) || "".equals(ext);
  }

  @Override
  public String importFile(@NonNull File file) throws IOException {
    return importFile(file, StandardCharsets.UTF_8.name());
  }

  private Collection<String> getArguments(File file, BigInteger address, String encoding) {
    return Arrays.asList("-stdout", "-lc", "lower", "-e", encoding, "-a", address.toString()
        , file.getAbsolutePath());
  }

  @Override
  public String importFile(@NonNull final File file, @NonNull final String encoding)
      throws IOException, CharacterCodingException {
    BigInteger address = BigInteger.valueOf(32768);//needs to enter manually
    final AddressDialog dialog = getAddressDialog();
    dialog.setLocationRelativeTo(null);
    if (dialog.showModal() == AddressDialog.OK)  {
      address = dialog.getAddress() == null ? address : dialog.getAddress();
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
      sb.append(new String(out.toByteArray(), encoding))
          .append(new String(err.toByteArray(), encoding));
      return sb.toString();
    } catch (CallException e) {
      log.error(e.getMessage(), e);
      throw new IOException(e);
    } finally {
      System.setOut(stdout);
      System.setErr(stderr);
    }
  }
}
