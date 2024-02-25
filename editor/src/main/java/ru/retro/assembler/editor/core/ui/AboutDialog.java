package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 24.02.2024
 */
@Slf4j
public class AboutDialog extends JDialog {
    private static final String TEXT = "<html><b><h2>%s %d.%d</h2></b><br>%s %s, %s %s<br>%s</html>";
    @Setter
    @Getter
    private int majorVersion = 1;

    @Setter
    @Getter
    private int minorVersion;

    @Getter
    @Setter
    private String buildVersion = "0001";

    @Getter
    @Setter
    private String buildOn = "13/02/2004";

    private JButton btnClose;

    private JLabel textLabel;

    public AboutDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        try {
            setIconImage(ResourceUtils.loadImage("/icon16x16/chip.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setTitle(Messages.get(Messages.ABOUT));
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
        btnClose = new JButton(Messages.get(Messages.CLOSE));
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
        return String.format(TEXT, Messages.get(Messages.CAPTION), majorVersion, minorVersion
                ,Messages.get(Messages.BUILD), buildVersion, Messages.get(Messages.BUILT_ON), buildOn
                , Messages.get(Messages.WRITTEN_BY));
    }

    public void showModal() {
        textLabel.setText(renderText());
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    private void initListeners() {
        btnClose.addActionListener(l -> AboutDialog.this.dispose());
    }
}
