package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
@Slf4j
public class CompilerPanel extends JPanel {
    @Getter
    private TextField compilerPathField;

    @Getter
    private JButton btnCompilerPath;

    @Getter
    private TextField outputPathField;

    @Getter
    private JButton btnOutputDirectory;

    @Getter
    private JCheckBox cbEmbedded;

    public CompilerPanel() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel label1 = new JLabel(Messages.getInstance().get(Messages.COMPILE_PATH));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(4, 4, 4, 4);
        add(label1, c);
        compilerPathField = new TextField(64);
        compilerPathField.setEditable(false);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(compilerPathField, c);
        btnCompilerPath = new JButton("");
        try {
            btnCompilerPath.setIcon(ResourceUtils.loadIcon("/icon16x16/open.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        btnCompilerPath.setToolTipText(Messages.getInstance().get(Messages.CHOOSE_DIRECTORY));
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        add(btnCompilerPath, c);
        cbEmbedded = new JCheckBox(Messages.getInstance().get(Messages.EMBEDDED));
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        add(cbEmbedded, c);
        JLabel label2 = new JLabel(Messages.getInstance().get(Messages.OUTPUT_DIRECTORY));
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 4, 0, 4);
        add(label2, c);
        outputPathField = new TextField(64);
        outputPathField.setEditable(false);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(outputPathField, c);
        btnOutputDirectory = new JButton("");
        try {
            btnOutputDirectory.setIcon(ResourceUtils.loadIcon("/icon16x16/open.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        btnOutputDirectory.setToolTipText(Messages.getInstance().get(Messages.CHOOSE_DIRECTORY));
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        add(btnOutputDirectory, c);
    }

    private void initListeners() {
        cbEmbedded.addChangeListener(e -> {
            compilerPathField.setEnabled(!cbEmbedded.isSelected());
            btnCompilerPath.setEnabled(!cbEmbedded.isSelected());
        });
    }

}
