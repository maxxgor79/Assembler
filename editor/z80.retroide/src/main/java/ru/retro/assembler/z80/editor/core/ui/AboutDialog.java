package ru.retro.assembler.z80.editor.core.ui;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;
import ru.retro.assembler.z80.editor.utils.ResourceUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Date;

/**
 * @Author: Maxim Gorin
 * Date: 24.02.2024
 */
@Slf4j
public class AboutDialog extends JDialog implements ModalDialog {
    private static final String TEXT = "<html><b><h2>%s %d.%d</h2></b><br>%s %04d, %s %tY-%tm-%td<br>%s</html>";
    @Setter
    @Getter
    private int majorVersion = 1;

    @Setter
    @Getter
    private int minorVersion;

    @Getter
    @Setter
    private long buildVersion;

    @Getter
    @Setter
    private Date buildDate;

    private JButton btnClose;

    private JLabel textLabel;

    public AboutDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        try {
            setIconImage(ResourceUtils.loadImage("/icon16x16/about.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setTitle(Z80Messages.getInstance().get(Z80Messages.ABOUT));
        setModal(true);
        setLayout(new BorderLayout());
        add(createImgPanel(), BorderLayout.WEST);
        add(createBtnPanel(), BorderLayout.SOUTH);
        add(createMainPanel(), BorderLayout.CENTER);
        pack();
        setResizable(false);
        getRootPane().setDefaultButton(btnClose);
        btnClose.requestFocus();
        initListeners();
    }

    private JPanel createBtnPanel() {
        final JPanel btnPanel = new JPanel();
        btnClose = new JButton(Z80Messages.getInstance().get(Z80Messages.CLOSE));
        btnPanel.add(btnClose);
        return btnPanel;
    }

    private JPanel createImgPanel() {
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        final JLabel iconWrapper = new JLabel();
        try {
            iconWrapper.setIcon(new ImageIcon(ResourceUtils.loadImage("/images/zxspectrum2.png")));
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        panel.add(iconWrapper);
        return panel;
    }

    private JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        textLabel = new JLabel(renderText());
        panel.add(textLabel);
        return panel;
    }

    private String renderText() {
        buildDate = buildDate != null ? buildDate : new Date();
        return String.format(TEXT, Z80Messages.getInstance().get(Z80Messages.CAPTION), majorVersion, minorVersion
                , Z80Messages.getInstance().get(Z80Messages.BUILD), buildVersion, Z80Messages.getInstance()
                        .get(Z80Messages.BUILT_DATE), buildDate, buildDate, buildDate, Z80Messages.getInstance()
                        .get(Z80Messages.WRITTEN_BY));
    }

    @Override
    public int showModal() {
        textLabel.setText(renderText());
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return 0;
    }

    private void initListeners() {
        btnClose.addActionListener(l -> AboutDialog.this.dispose());
    }
}
