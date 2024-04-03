package ru.retro.assembler.i8080.editor.core.compile;

import java.io.SequenceInputStream;
import java.util.concurrent.ExecutorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.ConsoleWriter;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.sys.Caller;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.ui.progress.SimpleWorker;
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

  private final AppSettings settings;

  private final MainWindow mainWindow;

  private final ExecutorService executor;

  public EmbeddedCompiling(@NonNull final Controller controller) {
    this.settings = controller.getSettings();
    this.mainWindow = controller.getMainWindow();
    this.executor = controller.getExecutor();
  }

  @Override
  public void compile(@NonNull Source src, String... args) {
    executor.execute(() -> {
      final SimpleWorker<Void> worker = new SimpleWorker<>(mainWindow) {

        @Override
        protected Void perform() throws Exception {
          final String outputDir = settings.getOutputDirectory();
          PipedOutputStream outPos = null;
          PipedOutputStream errPos = null;
          try {
            final List<String> argList = CLIUtils.toList(CLIUtils.ARG_OUTPUT, outputDir,
                toArgument(src, settings.getEncoding())
                , args);
            final PipedInputStream outPis = new PipedInputStream();
            outPos = new PipedOutputStream(outPis);
            final PipedInputStream errPis = new PipedInputStream();
            errPos = new PipedOutputStream(errPis);
            System.setOut(new PrintStream(outPos));
            System.setErr(new PrintStream(errPos));
            final ConsoleWriter consoleWriter = new ConsoleWriter(
                new SequenceInputStream(outPis, errPis)
                , mainWindow.getConsole().getArea());
            SwingUtilities.invokeLater(consoleWriter);
            Caller.call("ru.assembler.microsha.MicroshaAssembler", argList);
          } catch (Throwable t) {
            log.error(t.getMessage(), t);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainWindow,
                t.getMessage(), I8080Messages.getInstance().get(Messages.ERROR),
                JOptionPane.ERROR_MESSAGE
                , ResourceUtils.getErrorIcon()));
          } finally {
            IOUtils.closeQuietly(outPos);
            IOUtils.closeQuietly(errPos);
          }
          return null;
        }
      };
      try {
        worker.execute();
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    });
  }
}


