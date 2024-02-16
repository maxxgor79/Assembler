package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.settings.ResourceSettings;
import ru.assembler.zxspectrum.core.settings.DefaultSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Maxim Gorin
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCommandFDCB {

    private static final String INST1 = "RLC (IY+0)\nRRC (iy+1)\nRL (IY+2)\nRr (IY+3)\nSLA (IY+4)\nSRA (IY+5)\n" +
            "SRL (IY+6)\n";

    @Test
    void testCommands1() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST1.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x16);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x26);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3e);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST2 = "BIT 0,(IY+0)\nBIT 1,(IY+1)\nBIT 2,(IY+2)\nBIT 3,(IY+3)\nBIT 4,(IY+4)\n" +
            "BIT 5,(IY+5)\nBIT 6,(IY+6)\nBIT 7,(IY+7)\n" +
            "RES 0,(IY+7)\nRES 1,(IY+6)\nRES 2,(IY+5)\nRES 3,(IY+4)\nRES 4,(iy+3)\nRES 5,(Iy+2)\nRES 6,(iy+1)\nRES 7,(iy+0)\n" +
            "SET 0,(iy+0)\nSET 1,(iy+1)\nSET 2,(iy+2)\nSET 3,(iy+3)\nSET 4,(iy+4)\nSET 5,(iy+5)\nSET 6,(iy+6)\nSET 7,(iy+7)\n";

    @Test
    void testCommands2() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST2.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x46);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x56);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x66);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x76);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7e);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbe);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xde);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xee);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        log.info(Arrays.toString(bytes));
    }
}
