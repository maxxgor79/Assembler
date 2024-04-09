package ru.retro.assembler.editor.core.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.nio.charset.Charset;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import ru.retro.assembler.editor.core.i18n.Messages;

/**
 * StatusPanel.
 *
 * @author Maxim Gorin
 */
public class StatusPanel extends JPanel {

  protected static final int HEIGHT = 24;

  private static final String POSITION_PATTERN = "%d:%d";

  private JLabel posLabel;

  private JLabel encodingLabel;

  private JLabel lineEndingLabel;


  public StatusPanel() {
    initComponents();
  }

  private void initComponents() {
    final Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    setBorder(border);
    setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    lineEndingLabel = createEndingLabel();
    add(lineEndingLabel);
    encodingLabel = createEncodingLabel();
    add(encodingLabel);
    posLabel = createPosLabel();
    add(posLabel);
    setMinimumSize(new Dimension(HEIGHT, HEIGHT));
    setEncoding(Charset.defaultCharset().displayName());
    setPosition(1, 1);
  }

  private JLabel createPosLabel() {
    final JLabel label = new JLabel("");
    return label;
  }

  private JLabel createEncodingLabel() {
    final JLabel label = new JLabel("");
    return label;
  }

  private JLabel createEndingLabel() {
    final JLabel label = new JLabel("");
    return label;
  }

  public void setEncoding(String s) {
    encodingLabel.setText(s);
    encodingLabel.setToolTipText(String.format(Messages.getInstance().get(Messages.FILE_ENCODING), s));
  }

  public void setPosition(int row, int col) {
    posLabel.setText(String.format(POSITION_PATTERN, row, col));
    posLabel.setToolTipText(Messages.getInstance().get(Messages.CURSOR_POSITION));
  }

  public void setLineEnding(String s) {
    lineEndingLabel.setText(s);
    lineEndingLabel.setToolTipText(Messages.getInstance().get(Messages.LINE_SEPARATOR));
  }
}
