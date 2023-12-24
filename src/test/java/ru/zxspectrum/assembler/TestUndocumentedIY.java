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
public class TestUndocumentedIY {
    private static final String INST1 = "RL (Iy+1),A\nRR (Iy+2),B\nRR (Iy+3),C\nRR (Iy+$4),D\nRR (Iy+5h),E\n" +
            "RR(Iy+6h)\nRR(Iy+7h),A\n" +
            "SLA (IY+255),B\nSLA(IY+254),C\nSla(IY+253),D\nSLA (IY+252),E\nSLA (IY+251),H\nSLA (IY+250),L\nSLA (IY+249)\n" +
            "SLA (IY+248),a\n";

    @Test
    void testDDCBCommands1() throws IOException {
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x17);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x18);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x19);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x1f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x20);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x21);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x22);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x23);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x24);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x25);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x26);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x27);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST2 = "SLL (IY+0),B\nSLL (IY+1),c\nSLL (IY+2),D\nSLL (IY+3),E\nSLL (IY+4),h\n" +
            "SLL (IY+5),L\nSLL (IY+6)\nSLL (IY+7),A\n" +
            "srl(IY+07),B\nsrl(IY+06),C\nSrl(IY+05),d\nSrl(IY+04),e\nSrl(IY+03),h\nSrl(IY+02),l\nSrl(IY+01)\nSrl(IY+00),a\n";

    @Test
    void testDDCBCommands2() throws IOException {
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x30);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x31);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x32);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x34);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x35);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x36);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x37);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x38);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x39);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3f);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST3 = "RES 0,(IY+55),B\nRES 0,(IY+56),C\nRES 0,(IY+57),D\nRES 0,(IY+58),E\nRES 0,(IY+59),H\n" +
            "RES 0,(IY+60),L\nRES 0,(IY+61)\nRES 0,(IY+62),A\n" +
            "RES 1,(IY+0),B\nRES 1,(IY+1),C\nRES 1,(IY+2),D\nRES 1,(IY+3),E\nRES 1,(IY+4),H\nRES 1,(IY+5),L\n" +
            "RES 1,(IY+6)\nRES 1,(IY+7),A\n" +
            "Res 2,(IY+0),B\nRes 2,(IY+1),C\nres 2,(IY+2),D\nres 2,(IY+3),E\nRES 2,(IY+4),H\nRES 2,(IY+5),L\n" +
            "RES 2,(IY+6)\nRES 2,(IY+7),A\n" +
            "Res 3,(IY+44),B\nRes 3,(IY+45),C\nres 3,(IY+46),D\nres 3,(IY+47),E\nRES 3,(IY+48),H\nRES 3,(IY+49),L\n" +
            "RES 3,(IY+50)\nRES 3,(IY+51),A\n";

    @Test
    void testDDCBCommands3() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST3.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x37);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x38);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x81);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x39);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x82);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x83);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x84);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x85);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x87);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x88);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x89);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8f);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x90);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x91);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x92);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x93);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x94);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x95);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x97);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x98);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x99);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x30);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x31);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x32);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9f);

        log.info(Arrays.toString(bytes));
    }

    private static final String INST4 = "RES 4,(IY+55),B\nRES 4,(IY+56),C\nRES 4,(IY+57),D\nRES 4,(IY+58),E\nRES 4,(IY+59),H\n" +
            "RES 4,(IY+60),L\nRES 4,(IY+61)\nRES 4,(IY+62),A\n" +
            "RES 5,(IY+0),B\nRES 5,(IY+1),C\nRES 5,(IY+2),D\nRES 5,(IY+3),E\nRES 5,(IY+4),H\nRES 5,(IY+5),L\n" +
            "RES 5,(IY+6)\nRES 5,(IY+7),A\n" +
            "Res 6,(IY+0),B\nRes 6,(IY+1),C\nres 6,(IY+2),D\nres 6,(IY+3),E\nRES 6,(IY+4),H\nRES 6,(IY+5),L\n" +
            "RES 6,(IY+6)\nRES 6,(IY+7),A\n" +
            "Res 7,(IY+44),B\nRes 7,(IY+45),C\nres 7,(IY+46),D\nres 7,(IY+47),E\nRES 7,(IY+48),H\nRES 7,(IY+49),L\n" +
            "RES 7,(IY+50)\nRES 7,(IY+51),A\n";

    @Test
    void testDDCBCommands4() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST4.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x37);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x38);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x39);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xaa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xab);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xac);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xad);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xae);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xaf);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xba);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x30);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x31);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x32);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbf);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST5 = "RLC(IY+0),B\nRLC(IY+1),C\nRLC(IY+2),D\nRLC(IY+3),E\nRLC(IY+4),H\nRLC(IY+5),L\n"
            + "RLC(IY+6),A\n"
            + "RRC(IY+6),B\nRRC(IY+5),C\nRRC(IY+4),D\nRRC(IY+3),E\nRRC(IY+2),H\nRRC(IY+0b00000001),L\nRRC(IY+0),A\n"
            + "RL (IY+$0),B\nRL (IY+$1),C\nRL (IY+$2),D\nRL (IY+$3),E\nRL (IY+$4),H\nRL (IY+$5),L\nRL (IY+$6),A\n"
            + "SRA (IY+0h),B\nSRA (IY+1h),C\nSRA (IY+2h),D\nSRA (IY+3h),E\nSRA (IY+4h),H\nSRA (IY+5h),L\nSRA (IY+6h),A\n";

    @Test
    void testDDCBCommands5() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST5.getBytes());
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x07);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x08);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x09);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0f);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x10);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x11);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x12);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x13);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x14);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x15);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x17);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x28);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x29);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2f);

        log.info(Arrays.toString(bytes));
    }

    private static final String INST6 = "SET 0,(IY+0),B\nSET 0,(IY+01),C\nSET 0,(IY+02),D\nSET 0,(IY+03),E\nSET 0,(IY+04),H\n"
            + "SET 0,(IY+05),L\nSET 0,(IY+06),A\n"
            + "SET 1,(IY+0h),B\nSET 1,(IY+1h),C\nSET 1,(IY+2h),D\nSET 1,(IY+3h),E\nSET 1,(IY+4h),H\n"
            + "SET 1,(IY+5h),L\nSET 1,(IY+6h),A\n"
            + "SET 2,(IY+0x00),B\nSET 2,(IY+0x01),C\nSET 2,(IY+0x02),D\nSET 2,(IY+0x03),E\nSET 2,(IY+0x04),H\n"
            + "SET 2,(IY+0x05),L\nSET 2,(IY+0x06),A\n"
            + "SET 3,(IY+$00),B\nSET 3,(IY+$01),C\nSET 3,(IY+$02),D\nSET 3,(IY+$03),E\nSET 3,(IY+$04),H\n"
            + "SET 3,(IY+$05),L\nSET 3,(IY+$06),A\n";

    @Test
    void testDDCBCommands6() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST6.getBytes());
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xc9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xca);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcf);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xd9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xda);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xdf);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST7 = "SET 4,(IY+0b0),B\nSET 4,(IY+0B01),C\nSET 4,(IY+0b10),D\nSET 4,(IY+0B11),E\n"
            + "SET 4,(IY+0B100),H\nSET 4,(IY+0B101),L\nSET 4,(IY+0B110),A\n"
            + "SET 5,(IY+-1),B\nSET 5,(IY+-2),C\nSET 5,(IY+-3),D\nSET 5,(IY+-4),E\n"
            + "SET 5,(IY+-5),H\nSET 5,(IY+-6),L\nSET 5,(IY+-7),A\n"
            + "SET 6,(IY+-0x1),B\nSET 6,(IY+-0x2),C\nSET 6,(IY+-0x3),D\nSET 6,(IY+-0x4),E\n"
            + "SET 6,(IY+-0x5),H\nSET 6,(IY+-0x6),L\nSET 6,(IY+-0x7),A\n"
            + "SET 7,(IY+-01),B\nSET 7,(IY+-02),C\nSET 7,(IY+-03),D\nSET 7,(IY+-04),E\n"
            + "SET 7,(IY+-05),H\nSET 7,(IY+-06),L\nSET 7,(IY+-07),A\n";

    @Test
    void testDDCBCommands7() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST7.getBytes());
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
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x02);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x03);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x04);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x05);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x06);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xe9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xea);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xeb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xec);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xed);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xef);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf7);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfe);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfa);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xf9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST8 = "INC IYh\nDEC IYh\nLD IYh,0x7f\nINC IYl\ndec IYl\nLD IYl,15\nLD B,IYh\n" +
            "LD B,IYl\nLD C,IYh\nLD C,IYl\nLD d,IYh\nLD d,IYl\nLD E,IYh\nLD E,IYl\nLD IYh,B\nLD IYh,c\nLD iyh,d\n" +
            "LD IYh,e\nLD IYh,IYh\nLD IYh,IYl\nLD IYh,A\nLD IYl,B\nLD IYl,C\nLD IYl,D\nLD IYl,e\nLD IYl,IYh\nLD IYl,IYl\n" +
            "LD IYl,A\nLD A,IYh\nLD A,IYl\nADD A,IYh\nADD IYl\nADC A,IYh\nADC A,IYl\nSUB IYh\nSUB IYl\n";

    @Test
    void testDDCBCommands8() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST8.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x24);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x25);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x26);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x2e);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x0f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x44);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x45);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x4d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x54);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x55);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x5d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x60);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x61);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x62);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x63);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x64);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x65);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x67);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x68);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x69);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6a);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6b);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x6f);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x7d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x84);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x85);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x94);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x95);
        log.info(Arrays.toString(bytes));
    }

    private static final String INST9 = "SBC A,IYh\nSBC A,IYl\nAND IYh\nAND IYl\nXOR IYh\nXOR IYl\nOR IYh\nOR IYl\n" +
            "CP IYh\nCP IYl\n";

    @Test
    void testDDCBCommands9() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(INST9.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9c);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9d);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xa5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xac);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xad);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xb5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbc);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xfd);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xbd);
    }
}
