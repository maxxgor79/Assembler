package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.types.LineEnding;

import javax.swing.*;
import java.awt.*;

public class LineEndingPanel extends JPanel {
    @Getter
    private JComboBox<LineEnding> cbLineEnding;

    public LineEndingPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        final JLabel label = new JLabel(Messages.getInstance().get(Messages.LINE_SEPARATOR) + ":");
        add(label);
        cbLineEnding = new JComboBox<>();
        for (LineEnding le : LineEnding.values()) {
            cbLineEnding.addItem(le);
        }
        add(cbLineEnding);
    }
}
