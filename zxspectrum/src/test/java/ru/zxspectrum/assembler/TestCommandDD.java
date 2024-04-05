package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.io.FileDescriptor;
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
public class TestCommandDD {
    private static String SETTINGS_PATH = "asm/settings.properties";

    private static final String INST1 = "ADD IX,BC\nADD iX,DE\nADD IX,IX\nADD IX,SP\nLD IX,0x5000\nLd ($6000), IX\nINC iX\nDEC IX\nINC(IX+ffh)\n" +
            "DEC (IX+0x80)\nLD(IX+3),255\nLD B,(IX+1)\nLD C,(IX+2)\nLD d,(ix+3)\nld e,(ix+4)\nld h,(ix+5)\n" +
            "LD L,(IX+6)\nLD A,(IX+7)\n";

    @Test
    void testCommand1() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load(SETTINGS_PATH);
        ByteArrayInputStream bis = new ByteArrayInputStream(INST1.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new FileDescriptor(new File("test")), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x09);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x19);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x29);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x39);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x21);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x50);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x22);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x60);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x23);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x34);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x35);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x36);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x46);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x56);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x66);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
    }

    private static final String INST2 = "ld (ix +0),b\nld (ix +1) , c\nld (ix +2), d\nld (ix + 3),e\nld (ix+4),h\n" +
            "Ld (ix + 5),l\nlD (ix +6),a\nld a,(iX + 201)\nadd A,(IX+2)\nAdc A,(ix +3)\nsub (ix+0x80)\nsbc a,(ix+0x81)\n" +
            "and (ix+0x00)\n xor(ix+0x01)\n OR(ix+0x02)\nCP (ix+3)\npOp IX\nEX (sp),ix\npush ix\njp (ix)\nld  sp,IX\n";

    @Test
    void testCommand2() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load(SETTINGS_PATH);
        ByteArrayInputStream bis = new ByteArrayInputStream(INST2.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new FileDescriptor(new File("test")), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x70);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x71);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x72);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x73);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x74);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x75);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x77);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc9);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x81);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xae);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        log.info(Arrays.toString(bytes));
    }
}
