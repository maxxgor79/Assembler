package ru.retro.assembler.editor.core.util;

import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.ToolButton;

import java.util.Collection;

public interface ToolButtonFactory {
    Collection<ToolButton> newToolButtons(Controller controller);
}
