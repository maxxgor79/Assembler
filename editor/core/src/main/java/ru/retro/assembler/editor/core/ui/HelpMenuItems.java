package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
@Slf4j
public class HelpMenuItems {
    @Getter
    private JMenuItem miHelp;

    @Getter
    private JMenuItem miAbout;

    public HelpMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miHelp);
        menu.addSeparator();
        menu.add(miAbout);
    }

    private void initComponents() {
        miHelp = new JMenuItem(Messages.getInstance().get(Messages.HELP));
        miHelp.setMnemonic('H');
        try {
            miHelp.setIcon(ResourceUtils.loadIcon("/icon16x16/help.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miAbout = new JMenuItem(Messages.getInstance().get(Messages.ABOUT));
        miAbout.setMnemonic('A');
        try {
            miAbout.setIcon(ResourceUtils.loadIcon("/icon16x16/about.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
