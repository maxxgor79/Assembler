package ru.retro.assembler.editor.core.ui.player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 21.03.2024
 */
@Slf4j
public class ButtonsPanel extends JPanel {
    @Getter
    private JButton btnPlay;

    @Getter
    private JButton btnStop;

    public ButtonsPanel() {
        initComponents();
    }

    private void initComponents() {
        btnPlay = new JButton();
        try {
            btnPlay.setIcon(ResourceUtils.loadIcon("/icon32x32/play.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        btnPlay.setToolTipText(Messages.getInstance().get(Messages.PLAY));
        add(btnPlay);
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
