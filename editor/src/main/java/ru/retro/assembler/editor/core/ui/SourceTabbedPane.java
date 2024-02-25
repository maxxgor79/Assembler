package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
@Slf4j
public class SourceTabbedPane extends JTabbedPane {
    private final Map<Source, Integer> sourceMap = new HashMap<>();

    private Icon icon;

    private final Map<Integer, EditorPanel> editorPanelMap = new HashMap<>();

    private SourcePopupMenu sourcePopupMenu;

    public SourceTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        if (icon == null) {
            try  {
                icon = ResourceUtils.loadIcon("/icon16x16/text.png");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        sourcePopupMenu = new SourcePopupMenu();
        initListeners();
    }

    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Определяем индекс выделенной мышкой вкладки
                sourcePopupMenu.setLocation(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    sourcePopupMenu.show(SourceTabbedPane.this, e.getX(), e.getY());
                }
            }
        });
    }

    public boolean add(@NonNull final Source src) {
        if (sourceMap.containsKey(src)) {
            return false;
        }
        final EditorPanel editorPanel = new EditorPanel();
        int index = getTabCount();
        insertTab(src.getName(), icon, editorPanel, src.getName(), index);
        sourceMap.put(src, index);
        editorPanelMap.put(index, editorPanel);
        if (src.getContent() != null) {
            editorPanel.getTextArea().setText(src.getContent());
        }
        return true;
    }

    public boolean setSelected(@NonNull final Source src) {
        if (!sourceMap.containsKey(src)) {
            return false;
        }
        this.setSelectedIndex(sourceMap.get(src));
        return true;
    }

    public boolean close(@NonNull final Source src) {
        if (!sourceMap.containsKey(src)) {
            return false;
        }
        removeTabAt(sourceMap.get(src));
        editorPanelMap.remove(sourceMap.get(src));
        sourceMap.remove(src);
        return true;
    }
}
