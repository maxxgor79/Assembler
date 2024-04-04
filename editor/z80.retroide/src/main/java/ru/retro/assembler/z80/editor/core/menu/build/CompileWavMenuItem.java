package ru.retro.assembler.z80.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.ui.progress.SimpleWorker;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;
import ru.retro.assembler.z80.editor.utils.CLIUtils;

import java.awt.event.ActionEvent;

@Slf4j
public class CompileWavMenuItem extends AbstractCompileMenuItem {

  public CompileWavMenuItem(@NonNull Controller controller) {
    super(controller, Z80Messages.getInstance().get(Z80Messages.COMPILE_WAV), (char) 0, null, null);
  }

  @Override
  public int order() {
    return 4;
  }

  @Override
  public boolean hasSeparator() {
    return false;
  }

  @Override
  public void onAction(ActionEvent e) {
    log.info("Action compile into wave format");

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
  }
}
