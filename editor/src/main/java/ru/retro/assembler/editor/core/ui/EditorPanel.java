package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
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

    public EditorPanel() {
        initComponents();
    }

    private void initComponents() {
        final Border border = BorderFactory.createLoweredBevelBorder();
        setBorder(border);
        setLayout(new BorderLayout());
        textArea = createTextArea();
        final RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        try (FileInputStream fis = new FileInputStream("simple-monitor.asm")) {
            textArea.setText(new String(IOUtils.toByteArray(fis)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RSyntaxTextArea createTextArea() {
        final RSyntaxTextArea textArea = new RSyntaxTextArea(40, 80);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86);
        textArea.setCodeFoldingEnabled(true);
        return textArea;
    }
}
