package ru.retro.assembler.editor.core.settings;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.retro.assembler.editor.core.access.Setting;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class AppSettings extends Settings {
    @Getter
    @Setter
    @Setting
    protected int mainFramePosX;

    @Getter
    @Setter
    @Setting
    protected int mainFramePosY;

    @Getter
    @Setter
    @Setting
    protected int mainFrameWidth = 1024;

    @Getter
    @Setter
    @Setting
    protected int mainFrameHeight = 768;

    @Getter
    @Setter
    @Setting
    protected  int state;

    @Getter
    @Setter
    @Setting
    protected  int dividerLocation = 496;

    @Getter
    @Setter
    @Setting
    protected String helpUri = "http://something.ru";

    @Getter
    @Setter
    @Setting
    protected String compilerPath = "asm";

    @Getter
    @Setter
    @Setting
    protected String outputDirectory = "output";

    @Getter
    @Setter
    @Setting
    protected int majorVersion = 1;

    @Getter
    @Setter
    @Setting
    protected int minorVersion;

    @Getter
    @Setter
    @Setting
    protected String encoding = "UTF-8";

    @Getter
    @Setter
    @Setting
    @NonNull
    protected String openDialogCurrentDirectory;

    @Getter
    @Setter
    @Setting
    @NonNull
    protected String saveDialogCurrentDirectory;
}
