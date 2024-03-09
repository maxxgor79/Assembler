package ru.retro.assembler.editor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.retro.assembler.editor.core.settings.AppSettings;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestSettings {
    @Test
    public void test1Save() {
        final AppSettings settings = new AppSettings();
        settings.setMainFramePosX(1);
        settings.setMainFramePosY(2);
        settings.setMainFrameWidth(3);
        settings.setMainFrameHeight(4);
     }

}