package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class PreferencesTabbedPane extends JTabbedPane {
    @Getter
    private CompilerPanel compilerPanel;

    @Getter
    private MiscellaneousPanel miscellaneousPanel;

    @Getter
    private AppearancePanel appearancePanel;

    public PreferencesTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        compilerPanel = new CompilerPanel();
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel1.add(compilerPanel);
        addTab(Messages.getInstance().get(Messages.COMPILER), panel1);
        appearancePanel = new AppearancePanel();
        addTab(Messages.getInstance().get(Messages.APPEARANCE), new JScrollPane(appearancePanel));
        miscellaneousPanel = new MiscellaneousPanel();
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel2.add(miscellaneousPanel);
        addTab(Messages.getInstance().get(Messages.MISCELLANEOUS), panel2);
    }
}
