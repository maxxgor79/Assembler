package ru.retro.assembler.i8080.editor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.i8080.editor.core.menu.MenuItems;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildToolButtons;
import ru.retro.assembler.i8080.editor.core.settings.I8080AppSettings;
import ru.retro.assembler.i8080.editor.core.ui.FileChoosers;
import ru.retro.assembler.i8080.editor.core.ui.UIComponents;

@Slf4j
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
        Controller.setAppSettingsFactory(() -> new I8080AppSettings() {
            @Override
            public String getPrefix() {
                return "i8080";
            }
        });
        Controller.setToolButtonFactory(BuildToolButtons.defaultToolButtonFactory());
        Controller.setMenuItemFactory(MenuItems.defaultMenuItemFactory());
        Controller.setFileChooserFactory(FileChoosers.defaultFileChooserFactory());
        Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
    }

    private static void printSystemInfo() {
        final String message = "Running on " + System.getProperty("os.name") + " version " + System
                .getProperty("os.version");
        log.info(message);
    }

    public static void main(String[] args) {
        printSystemInfo();
        setDefaultExt();
        setDefaultFactories();
        final Editor editor = new Editor();
        editor.entry(args);
    }
}
