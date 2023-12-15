package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.settings.ConstantSettings;
import ru.zxspectrum.assembler.settings.ResourceSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCommand {
    private static final String BASIC_INST1 = "nop\nld bc,8000h\nld (bc),a\ninc bc\ninc b\ndec b\nld b,120\nrlca\n" +
            "ex af,af'\nadd hl, bc\nld a,(bc)\ndec bc\ninc c\ndec c\nld c,0xff\nrrca\ndjnz 32768\nld de,$1313\n" +
            "ld (de),a\ninc de\ninc d\ndec d\nld d,60h\nrla\njr 32768\nadd hl,de\nld a,(de)\ndec de\ninc e\ndec e\n" +
            "ld e,$11\nrra\njr nz, 32768\nld hl,0x4040\nld(0x6363),hl\ninc hl\ninc h\ndec h\nld h,$15\ndaa\n" +
            "jr z, 32768\nadd hl,hl\nld hl,(16383)\ndec hl\ninc l\ndec l\nld l,$22\ncpl\njr nc, 32768\nld sp, 7777\n" +
            "ld (0x9999), A\ninc sp\ninc (hl)\ndec(hl)\nld(hl),$55\nSCF\njr c,32768\nadd hl,sp\nld a,($1111)\n" +
            "dec sp\ninc a\nDEC A\nLd a,  1\nccf\n";

    @Test
    void testBasicCommands1() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST1.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new ConstantSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++], 0);
        Assertions.assertEquals(bytes[pc++], 1);
        Assertions.assertEquals(bytes[pc++], 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++], 0x02);
        Assertions.assertEquals(bytes[pc++], 0x03);
        Assertions.assertEquals(bytes[pc++], 0x04);
        Assertions.assertEquals(bytes[pc++], 0x05);
        Assertions.assertEquals(bytes[pc++], 0x06);
        Assertions.assertEquals(bytes[pc++], 120);
        Assertions.assertEquals(bytes[pc++], 0x07);
        Assertions.assertEquals(bytes[pc++], 0x08);
        Assertions.assertEquals(bytes[pc++], 0x09);
        Assertions.assertEquals(bytes[pc++], 0x0A);
        Assertions.assertEquals(bytes[pc++], 0x0B);
        Assertions.assertEquals(bytes[pc++], 0x0C);
        Assertions.assertEquals(bytes[pc++], 0x0D);
        Assertions.assertEquals(bytes[pc++], 0x0E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xff);
        Assertions.assertEquals(bytes[pc++], 0x0F);
        int codeOff = namespace.getCurrentCodeOffset().intValue();
        Assertions.assertEquals(bytes[pc++], 0x10);
        Assertions.assertEquals(bytes[pc++], -22);
        Assertions.assertEquals(bytes[pc++], 0x11);
        Assertions.assertEquals(bytes[pc++], 0x13);
        Assertions.assertEquals(bytes[pc++], 0x13);
        Assertions.assertEquals(bytes[pc++], 0x12);
        Assertions.assertEquals(bytes[pc++], 0x13);//inc de
        Assertions.assertEquals(bytes[pc++], 0x14);
        Assertions.assertEquals(bytes[pc++], 0x15);
        Assertions.assertEquals(bytes[pc++], 0x16);
        Assertions.assertEquals(bytes[pc++], 0x60);
        Assertions.assertEquals(bytes[pc++], 0x17);
        Assertions.assertEquals(bytes[pc++], 0x18);
        Assertions.assertEquals(bytes[pc++], -34);
        Assertions.assertEquals(bytes[pc++], 0x19);
        Assertions.assertEquals(bytes[pc++], 0x1A);
        Assertions.assertEquals(bytes[pc++], 0x1B);
        Assertions.assertEquals(bytes[pc++], 0x1C);
        Assertions.assertEquals(bytes[pc++], 0x1D);
        Assertions.assertEquals(bytes[pc++], 0x1E);
        Assertions.assertEquals(bytes[pc++], 0x11);
        Assertions.assertEquals(bytes[pc++], 0x1F);
        Assertions.assertEquals(bytes[pc++], 0x20);
        Assertions.assertEquals(bytes[pc++], -44);
        Assertions.assertEquals(bytes[pc++], 0x21);
        Assertions.assertEquals(bytes[pc++], 0x40);
        Assertions.assertEquals(bytes[pc++], 0x40);
        Assertions.assertEquals(bytes[pc++], 0x22);
        Assertions.assertEquals(bytes[pc++], 0x63);
        Assertions.assertEquals(bytes[pc++], 0x63);
        Assertions.assertEquals(bytes[pc++], 0x23);
        Assertions.assertEquals(bytes[pc++], 0x24);
        Assertions.assertEquals(bytes[pc++], 0x25);
        Assertions.assertEquals(bytes[pc++], 0x26);
        Assertions.assertEquals(bytes[pc++], 0x15);
        Assertions.assertEquals(bytes[pc++], 0x27);
        Assertions.assertEquals(bytes[pc++], 0x28);
        Assertions.assertEquals(bytes[pc++], -58);
        Assertions.assertEquals(bytes[pc++], 0x29);
        Assertions.assertEquals(bytes[pc++], 0x2A);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x3F);
        Assertions.assertEquals(bytes[pc++], 0x2B);
        Assertions.assertEquals(bytes[pc++], 0x2C);
        Assertions.assertEquals(bytes[pc++], 0x2D);
        Assertions.assertEquals(bytes[pc++], 0x2E);
        Assertions.assertEquals(bytes[pc++], 0x22);
        Assertions.assertEquals(bytes[pc++], 0x2F);
        Assertions.assertEquals(bytes[pc++], 0x30);
        Assertions.assertEquals(bytes[pc++], -70);
        Assertions.assertEquals(bytes[pc++], 0x31);
        Assertions.assertEquals(bytes[pc++], 97);
        Assertions.assertEquals(bytes[pc++], 30);
        Assertions.assertEquals(bytes[pc++], 0x32);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x99);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x99);
        Assertions.assertEquals(bytes[pc++], 0x33);
        Assertions.assertEquals(bytes[pc++], 0x34);
        Assertions.assertEquals(bytes[pc++], 0x35);
        Assertions.assertEquals(bytes[pc++], 0x36);
        Assertions.assertEquals(bytes[pc++], 0x55);
        Assertions.assertEquals(bytes[pc++], 0x37);
        Assertions.assertEquals(bytes[pc++], 0x38);
        Assertions.assertEquals(bytes[pc++], -84);
        Assertions.assertEquals(bytes[pc++], 0x39);
        Assertions.assertEquals(bytes[pc++], 0x3A);
        Assertions.assertEquals(bytes[pc++], 0x11);
        Assertions.assertEquals(bytes[pc++], 0x11);
        Assertions.assertEquals(bytes[pc++], 0x3B);
        Assertions.assertEquals(bytes[pc++], 0x3C);
        Assertions.assertEquals(bytes[pc++], 0x3D);
        Assertions.assertEquals(bytes[pc++], 0x3E);
        Assertions.assertEquals(bytes[pc++], 1);
        Assertions.assertEquals(bytes[pc++], 0x3F);
        /*
        System.out.println(bytes[pc++]);
        System.out.println(bytes[pc++]);
        System.out.println(bytes[pc++]);


         */
        log.info(Arrays.toString(bytes));
    }

    private static final String BASIC_INST2 = "";
    @Test
    void testBasicCommands2() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST1.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new ConstantSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        //Assertions.assertEquals(bytes[pc++], 0);
        log.info(Arrays.toString(bytes));
    }
}
