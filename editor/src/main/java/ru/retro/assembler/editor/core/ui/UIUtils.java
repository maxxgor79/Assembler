package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2024
 */
public final class UIUtils {
    private UIUtils() {

    }

    public static int getLine(@NonNull JTextArea textArea) throws BadLocationException {
        return textArea.getLineOfOffset(textArea.getCaretPosition());
    }

    public static int getColumn(@NonNull JTextArea textArea, int line) throws BadLocationException {
        return textArea.getCaretPosition() - textArea.getLineStartOffset(line);
    }
}
