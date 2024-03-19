package ru.retro.assembler.i8080.editor;

import lombok.Getter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildMenuItems;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildToolButtons;
import ru.retro.assembler.i8080.editor.core.ui.FileChoosers;
import ru.retro.assembler.i8080.editor.core.ui.UIComponents;

public class I8080Editor {
    @Getter
    private static final BuildVersionReader buildVersionReader = new BuildVersionReader();
    static {
        buildVersionReader.loadFromResource("/build.version");
    }

    private static void setDefaultExt() {
        UIUtils.putExt("asm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_I8080);
        UIUtils.putExt("mcsasm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_I8080);
        UIUtils.putExt("hasm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_I8080);
    }

    private static void setDefaultFactories() {
        Controller.setToolButtonFactory(BuildToolButtons.defaultToolButtonFactory());
        Controller.setMenuItemFactory(BuildMenuItems.defaultMenuItemFactory());
        Controller.setFileChooserFactory(FileChoosers.defaultFileChooserFactory());
        Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
    }

    public static void main(String [] args) {
        setDefaultExt();
        setDefaultFactories();
        final Editor editor = new Editor();
        editor.entry(args);
    }
}
