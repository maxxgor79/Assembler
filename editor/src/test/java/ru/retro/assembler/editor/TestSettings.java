package ru.retro.assembler.editor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.settings.Settings;

import java.io.ByteArrayOutputStream;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestSettings {
    @Test
    public void test1Save() throws ConfigurationException {
        final AppSettings settings = new AppSettings();
        settings.setMainFramePosX(1);
        settings.setMainFramePosY(2);
        settings.setMainFrameWidth(3);
        settings.setMainFrameHeight(4);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        settings.save(baos);
        String s = new String(baos.toByteArray());
    }

}