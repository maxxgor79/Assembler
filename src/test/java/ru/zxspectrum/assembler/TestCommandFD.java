package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.settings.DefaultSettings;
import ru.zxspectrum.assembler.settings.ResourceSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCommandFD {

    private static final String INST1 = "ADD IY,BC\nADD iY,DE\nADD IY,IY\nADD IY,SP\nLD IY,0x5000\nLd ($6000), IY\nINC iY\nDEC IY\nINC(IY+ffh)\n" +
            "DEC (IY+0x80)\nLD(IY+3),255\nLD B,(IY+1)\nLD C,(IY+2)\nLD d,(iy+3)\nld e,(iy+4)\nld h,(iy+5)\n" +
            "LD L,(IY+6)\nLD A,(IY+7)\n";

    //@Test
    void testCommand1() throws IOException {
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x09);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x19);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x29);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x39);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x21);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x50);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x22);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x60);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x23);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x34);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x35);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x36);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x46);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x56);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x66);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
    }

    private static final String INST2 = "ld (iy +0),b\nld (iy +1) , c\nld (iy +2), d\nld (iy + 3),e\nld (iy+4),h\n" +
            "Ld (iy + 5),l\nlD (Iy +6),a\nld a,(iY + 201)\nadd A,(IY+2)\nAdc A,(iy +3)\nsub (iy+0x80)\nsbc a,(iy+0x81)\n" +
            "and (iy+0x00)\n xor(iy+0x01)\n OR(iy+0x02)\nCP (iy+3)\npOp IY\nEX (sp),iy\npush iy\njp (iy)\nld  sp,IY\n";

    @Test
    void testCommand2() throws IOException {
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x70);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x71);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x72);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x73);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x74);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x75);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x77);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc9);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x81);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xae);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        log.info(Arrays.toString(bytes));
    }
}
