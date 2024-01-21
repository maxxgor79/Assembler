package ru.assembler.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.settings.DefaultSettings;
import ru.assembler.core.settings.ResourceSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCommandDDCB {
    private static final String INST1 = "RLC (IX+0)\nRRC (ix+1)\nRL (IX+2)\nRr (IX+3)\nSLA (IX+4)\nSRA (IX+5)\n" +
            "SRL (IX+6)\n";
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x16);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x26);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3e);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST2 = "BIT 0,(IX+0)\nBIT 1,(IX+1)\nBIT 2,(IX+2)\nBIT 3,(IX+3)\nBIT 4,(IX+4)\n" +
            "BIT 5,(IX+5)\nBIT 6,(IX+6)\nBIT 7,(IX+7)\n" +
            "RES 0,(ix+7)\nRES 1,(ix+6)\nRES 2,(ix+5)\nRES 3,(ix+4)\nRES 4,(ix+3)\nRES 5,(ix+2)\nRES 6,(ix+1)\nRES 7,(ix+0)\n" +
            "SET 0,(ix+0)\nSET 1,(ix+1)\nSET 2,(ix+2)\nSET 3,(ix+3)\nSET 4,(ix+4)\nSET 5,(ix+5)\nSET 6,(ix+6)\nSET 7,(ix+7)\n";
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x46);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x56);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x66);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x76);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7e);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBe);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xee);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        log.info(Arrays.toString(bytes));
    }
}
