package ru.retro.assembler.i8080.editor.core.compile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.ConsoleWriter;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;
import ru.retro.assembler.i8080.editor.utils.CLIUtils;

import javax.swing.*;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Slf4j
public class EmbeddedCompiling implements Compiling {

  private AppSettings settings;

  private MainWindow mainWindow;

  public EmbeddedCompiling(@NonNull final AppSettings settings,
      @NonNull final MainWindow mainWindow) {
    this.settings = settings;
    this.mainWindow = mainWindow;
  }

  @Override
  public void compile(@NonNull Source src, String... args) {
    final Class asmClazz = getAssemblerClazz();
    if (asmClazz == null) {
      JOptionPane.showMessageDialog(mainWindow,
              I8080Messages.getInstance().get(I8080Messages.EMBEDDED_NOT_FOUND), I8080Messages.getInstance()
                      .get(Messages.ERROR), JOptionPane.ERROR_MESSAGE, ResourceUtils.getErrorIcon());
      return;
    }
    final String outputDir = settings.getOutputDirectory();
    try {
      final List<String> argList = CLIUtils.toList(CLIUtils.ARG_OUTPUT, outputDir, toArgument(src, settings.getEncoding())
              , args);
      final Method method = asmClazz.getMethod("entry", Collection.class);
      if (method == null) {
        log.info("entry method not found");
        throw new NoSuchMethodException("entry");
      }
      final PipedInputStream pis = new PipedInputStream();
      final PipedOutputStream pos = new PipedOutputStream(pis);
      System.setOut(new PrintStream(pos));
      final ConsoleWriter consoleWriter = new ConsoleWriter(pis, mainWindow.getConsole().getArea());
      SwingUtilities.invokeLater(consoleWriter);
      method.invoke(null, argList);
      IOUtils.closeQuietly(pos);
    } catch (Throwable t) {
      log.error(t.getMessage(), t);
      JOptionPane.showMessageDialog(mainWindow,
          t.getMessage(), I8080Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE
          , ResourceUtils.getErrorIcon());
    }
  }

  private static Class getAssemblerClazz() {
    try {
      final Class clazz = Class.forName("ru.assembler.microsha.MicroshaAssembler");
      return clazz;
    } catch (ClassNotFoundException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
}
