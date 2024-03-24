package ru.retro.assembler.i8080.editor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildMenuItems;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildToolButtons;
import ru.retro.assembler.i8080.editor.core.ui.FileChoosers;
import ru.retro.assembler.i8080.editor.core.ui.UIComponents;
import ru.retro.assembler.i8080.editor.utils.ResourceUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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

    private static AppSettings createSettings() {
        final AppSettings instance = new AppSettings() {
            @Override
            public String getPrefix() {
                return "i8080";
            }
        };
        instance.setOutputDirectory("${user.home}${file.separator}i8080${file.separator}output");
        instance.setCompilerPath("mscasm");
        return instance;
    }

    private static void setDefaultFactories() {
        Controller.setToolButtonFactory(BuildToolButtons.defaultToolButtonFactory());
        Controller.setMenuItemFactory(BuildMenuItems.defaultMenuItemFactory());
        Controller.setFileChooserFactory(FileChoosers.defaultFileChooserFactory());
        Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
        Controller.setAppSettingsFactory(() -> createSettings());
    }

    private static void printSystemInfo() {
        final String message = "Running on " + System.getProperty("os.name") + " version " + System
                .getProperty("os.version");
        log.info(message);
    }

    private static void loadFonts() {
        byte [] data;
        try {
            data = ResourceUtils.loadResource("/font/Microsha.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        //ResourceUtils.toPng(args[0]);
        loadFonts();
        printSystemInfo();
        setDefaultExt();
        setDefaultFactories();
        final Editor editor = new Editor();
        editor.entry(args);
    }
}
