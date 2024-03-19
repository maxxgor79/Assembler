package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
@Slf4j
public class EditorPanel extends JPanel {
    @Getter
    private RSyntaxTextArea textArea;

    public EditorPanel(@NonNull final RSyntaxTextArea textArea) {
        this.textArea = textArea;
        initComponents();
    }

    private void initComponents() {
        final Border border = BorderFactory.createLoweredBevelBorder();
        setBorder(border);
        setLayout(new BorderLayout());
        final RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}
