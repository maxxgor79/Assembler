package ru.retro.assembler.editor.core.ui;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
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
    private final Map<Source, Link> sourceLinkMap = new HashMap<>();

    private Icon icon;

    private final Map<Integer, Source> indexSourceMap = new HashMap<>();

    @Getter
    private SourcePopupMenu sourcePopupMenu;

    public SourceTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        if (icon == null) {
            try {
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
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    sourcePopupMenu.show(SourceTabbedPane.this, e.getX(), e.getY());
                }
            }
        });
    }

    public RSyntaxTextArea add(@NonNull final Source src) {
        if (sourceLinkMap.containsKey(src)) {
            return null;
        }
        final EditorPanel editorPanel = new EditorPanel();
        final int index = getTabCount();
        insertTab(src.getName(), icon, editorPanel, src.getName(), index);
        sourceLinkMap.put(src, new Link(index, editorPanel.getTextArea()));
        indexSourceMap.put(index, src);
        if (src.getContent() != null) {
            editorPanel.getTextArea().setText(src.getContent());
        }
        return editorPanel.getTextArea();
    }

    public boolean setSelected(@NonNull final Source src) {
        if (!sourceLinkMap.containsKey(src)) {
            return false;
        }
        this.setSelectedIndex(sourceLinkMap.get(src).getIndex());
        return true;
    }

    public boolean close(@NonNull final Source src) {
        if (!sourceLinkMap.containsKey(src)) {
            return false;
        }
        final int index = sourceLinkMap.get(src).getIndex();
        removeTabAt(index);
        indexSourceMap.remove(index);
        sourceLinkMap.remove(src);
        return true;
    }

    public Source getSourceSelected() {
        return getSourceSelected(getSelectedIndex());
    }

    public Source getSourceSelected(int index) {
        return indexSourceMap.get(index);
    }

    public RSyntaxTextArea getSelectedTextArea() {
        return getTextArea(getSelectedIndex());
    }

    public RSyntaxTextArea getTextArea(int index) {
        final Source src = indexSourceMap.get(index);
        if (src == null) {
            return null;
        }
        return sourceLinkMap.get(src).getTextArea();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    protected static class Link {
        @Getter
        @Setter
        private int index;

        @Getter
        @Setter
        @NonNull
        private RSyntaxTextArea textArea;
    }
}
