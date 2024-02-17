package ru.assembler.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Maxim Gorin
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestImage {
    @Test
    public void testBitmap() throws IOException {
        BufferedImage img = ImageIO.read(new File("src/test/java/ru/assembler/core/res/img.png"));
        img = ImageUtils.toMonocrome(img);
        byte[] data = ImageUtils.toBytes(img);

    }
}
