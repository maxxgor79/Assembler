package ru.retro.assembler.editor.core.ui.record;

import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

/**
 * ButtonsPanel.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class ButtonsPanel extends JPanel {
  @Getter
  private JButton btnRecord;

  @Getter
  private JButton btnStop;

  public ButtonsPanel() {
    initComponents();
  }

  private void initComponents() {
    btnRecord = new JButton();
    try {
      btnRecord.setIcon(ResourceUtils.loadIcon("/icon32x32/record.png"));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    btnRecord.setToolTipText(Messages.getInstance().get(Messages.RECORD));
    add(btnRecord);
    btnStop = new JButton();
    try {
      btnStop.setIcon(ResourceUtils.loadIcon("/icon32x32/stop.png"));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    btnStop.setToolTipText(Messages.getInstance().get(Messages.STOP));
    add(btnStop);
  }
}
