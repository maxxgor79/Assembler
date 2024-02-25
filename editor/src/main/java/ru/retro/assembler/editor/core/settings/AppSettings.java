package ru.retro.assembler.editor.core.settings;

import lombok.Getter;
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
    protected int mainFrameWidth;

    @Getter
    @Setter
    @Setting
    protected int mainFrameHeight;

    @Getter
    @Setter
    @Setting
    protected  int state;

    @Getter
    @Setter
    @Setting
    protected  int dividerLocation;

    @Getter
    @Setter
    @Setting
    protected String helpUri;

    @Getter
    @Setter
    @Setting
    protected String compilerPath;

    @Getter
    @Setter
    @Setting
    protected String outputDirectory;

    @Getter
    @Setter
    @Setting
    protected int majorVersion;

    @Getter
    @Setter
    @Setting
    protected int minorVersion;
}
