package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class CompilerPanel extends JPanel {
    @Getter
    private TextField compilerPathField;

    @Getter
    private JButton btnCompilerPath;

    @Getter
    private TextField outputPathField;

    @Getter
    private JButton btnOutputDirectory;

    public CompilerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel label1 = new JLabel(Messages.get(Messages.COMPILE_PATH));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 4,0, 4);
        add(label1, c);
        compilerPathField = new TextField(64);
        compilerPathField.setEditable(false);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(compilerPathField, c);
        btnCompilerPath = new JButton("...");
        c.gridx = 2;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        add(btnCompilerPath, c);

        JLabel label2 = new JLabel(Messages.get(Messages.OUTPUT_DIRECTORY));
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 4,0, 4);
        add(label2, c);
        outputPathField = new TextField(64);
        outputPathField.setEditable(false);
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(outputPathField, c);
        btnOutputDirectory = new JButton("...");
        c.gridx = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        add(btnOutputDirectory, c);
    }
}
