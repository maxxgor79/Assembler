package ru.retro.assembler.i8080.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.ui.progress.SimpleWorker;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;
import ru.retro.assembler.i8080.editor.utils.CLIUtils;

import java.awt.event.ActionEvent;

@Slf4j
public class CompileWavMenuItem extends AbstractCompileMenuItem {

  public CompileWavMenuItem(@NonNull Controller controller) {
    super(controller, I8080Messages.getInstance().get(I8080Messages.COMPILE_WAV), (char) 0, null
        , null);
  }

  @Override
  public int order() {
    return 2;
  }

  @Override
  public boolean hasSeparator() {
    return false;
  }

  @Override
  public void onAction(ActionEvent e) {
    log.info("Action compile into wave format");
    controller.getExecutor().execute(() -> {
      final SimpleWorker<Void> worker = new SimpleWorker<>(controller.getMainWindow()) {

        @Override
        protected Void perform() throws Exception {
          final Source selectedSource = controller.getMainWindow().getSourceTabbedPane()
              .getSourceSelected();
          if (selectedSource == null) {
            return null;
          }
          compile(selectedSource, CLIUtils.ARG_WAV);
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
