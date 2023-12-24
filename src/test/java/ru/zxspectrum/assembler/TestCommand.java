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
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
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
        Assertions.assertEquals(bytes[pc++] & 0xff, 0x22);///llll
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
        log.info(Arrays.toString(bytes));
    }

    private static final String BASIC_INST2 = "LD B,B\nLD B,C\nLD B,D\nLD B,E\nLD B,H\nLD B,L\nLD B,(HL)\nLD B,A\n" +
            "LD C,B\nLD C,C\nLD C,D\nLD C,E\nLD C,H\nLD C,L\nLD C,(HL)\nLD C,A\nLD D,B\nLD D,C\nLD D,D\nLD D,E\nLD D,H\n" +
            "LD D,L\nLD D,(HL)\nLD D,A\nLD E,B\nLD E,C\nLD E,D\nLD E,E\nLD E,H\nLD E,L\nLD E,(HL)\nLD E,A\nLD H,B\n" +
            "LD H,C\nLD H,D\nLD H,E\nLD H,H\nLD H,L\nLD H,(HL)\nLD H,A\nLD L,B\nLD L,C\nLD L,D\nLD L,E\nLD L,H\nLD L,L\n" +
            "LD L,(HL)\nLD L,A\nLD (HL),B\nLD (HL),C\nLD (HL),D\nLD (HL),E\nLD (HL),H\nLD (HL),L\nHALT\nLD (HL),A\n" +
            "LD A,B\nLD A,C\nLD A,D\nLD A,E\nLD A,H\nLD A,L\nLD A,(HL)\nLD A,A";

    @Test
    void testBasicCommands2() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST2.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++], 0x40);
        Assertions.assertEquals(bytes[pc++], 0x41);
        Assertions.assertEquals(bytes[pc++], 0x42);
        Assertions.assertEquals(bytes[pc++], 0x43);
        Assertions.assertEquals(bytes[pc++], 0x44);
        Assertions.assertEquals(bytes[pc++], 0x45);
        Assertions.assertEquals(bytes[pc++], 0x46);
        Assertions.assertEquals(bytes[pc++], 0x47);
        Assertions.assertEquals(bytes[pc++], 0x48);
        Assertions.assertEquals(bytes[pc++], 0x49);
        Assertions.assertEquals(bytes[pc++], 0x4A);
        Assertions.assertEquals(bytes[pc++], 0x4B);
        Assertions.assertEquals(bytes[pc++], 0x4C);
        Assertions.assertEquals(bytes[pc++], 0x4D);
        Assertions.assertEquals(bytes[pc++], 0x4E);
        Assertions.assertEquals(bytes[pc++], 0x4F);

        Assertions.assertEquals(bytes[pc++], 0x50);
        Assertions.assertEquals(bytes[pc++], 0x51);
        Assertions.assertEquals(bytes[pc++], 0x52);
        Assertions.assertEquals(bytes[pc++], 0x53);
        Assertions.assertEquals(bytes[pc++], 0x54);
        Assertions.assertEquals(bytes[pc++], 0x55);
        Assertions.assertEquals(bytes[pc++], 0x56);
        Assertions.assertEquals(bytes[pc++], 0x57);
        Assertions.assertEquals(bytes[pc++], 0x58);
        Assertions.assertEquals(bytes[pc++], 0x59);
        Assertions.assertEquals(bytes[pc++], 0x5A);
        Assertions.assertEquals(bytes[pc++], 0x5B);
        Assertions.assertEquals(bytes[pc++], 0x5C);
        Assertions.assertEquals(bytes[pc++], 0x5D);
        Assertions.assertEquals(bytes[pc++], 0x5E);
        Assertions.assertEquals(bytes[pc++], 0x5F);

        Assertions.assertEquals(bytes[pc++], 0x60);
        Assertions.assertEquals(bytes[pc++], 0x61);
        Assertions.assertEquals(bytes[pc++], 0x62);
        Assertions.assertEquals(bytes[pc++], 0x63);
        Assertions.assertEquals(bytes[pc++], 0x64);
        Assertions.assertEquals(bytes[pc++], 0x65);
        Assertions.assertEquals(bytes[pc++], 0x66);
        Assertions.assertEquals(bytes[pc++], 0x67);
        Assertions.assertEquals(bytes[pc++], 0x68);
        Assertions.assertEquals(bytes[pc++], 0x69);
        Assertions.assertEquals(bytes[pc++], 0x6A);
        Assertions.assertEquals(bytes[pc++], 0x6B);
        Assertions.assertEquals(bytes[pc++], 0x6C);
        Assertions.assertEquals(bytes[pc++], 0x6D);
        Assertions.assertEquals(bytes[pc++], 0x6E);
        Assertions.assertEquals(bytes[pc++], 0x6F);

        Assertions.assertEquals(bytes[pc++], 0x70);
        Assertions.assertEquals(bytes[pc++], 0x71);
        Assertions.assertEquals(bytes[pc++], 0x72);
        Assertions.assertEquals(bytes[pc++], 0x73);
        Assertions.assertEquals(bytes[pc++], 0x74);
        Assertions.assertEquals(bytes[pc++], 0x75);
        Assertions.assertEquals(bytes[pc++], 0x76);
        Assertions.assertEquals(bytes[pc++], 0x77);
        Assertions.assertEquals(bytes[pc++], 0x78);
        Assertions.assertEquals(bytes[pc++], 0x79);
        Assertions.assertEquals(bytes[pc++], 0x7A);
        Assertions.assertEquals(bytes[pc++], 0x7B);
        Assertions.assertEquals(bytes[pc++], 0x7C);
        Assertions.assertEquals(bytes[pc++], 0x7D);
        Assertions.assertEquals(bytes[pc++], 0x7E);
        Assertions.assertEquals(bytes[pc++], 0x7F);
        log.info(Arrays.toString(bytes));
    }

    private static final String BASIC_INST3 = "ADD A,B\nADD A,C\nADD A,D\nADD A,E\nADD A,H\nADD A,L\nADD A,(HL)\n" +
            "ADD A,A\nADC A,B\nADC A,C\nADC A,D\nADC A,E\nADC A,H\nADC A,L\nADC A,(HL)\nADC A,A\nSUB A,B\nSUB A,C\n" +
            "SUB A,D\nSUB A,E\nSUB A,H\nSUB A,L\nSUB A,(HL)\nSUB A,A\nSBC A,B\nSBC A,C\nSBC A,D\nSBC A,E\nSBC A,H\n" +
            "SBC A,L\nSBC A,(HL)\nSBC A,A\nAND A,B\nAND A,C\nAND A,D\nAND A,E\nAND A,H\nAND A,L\nAND A,(HL)\nAND A,A\n" +
            "XOR A,B\nXOR A,C\nXOR A,D\nXOR A,E\nXOR A,H\nXOR A,L\nXOR A,(HL)\nXOR A,A\nOR A,B\nOR A,C\nOR A,D\nOR A,E\n" +
            "OR A,H\nOR A,L\nOR A,(HL)\nOR A,A\nCP A,B\nCP A,C\nCP A,D\nCP A,E\nCP A,H\nCP A,L\nCP A,(HL)\nCP A,A\n";

    @Test
    void testBasicCommands3() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST3.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x81);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x82);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x83);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x84);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x85);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x86);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x87);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x88);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x89);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8A);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8B);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8C);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8D);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x8F);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x90);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x91);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x92);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x93);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x94);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x95);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x96);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x97);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x98);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x99);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9A);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9B);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9C);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9D);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9E);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x9F);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xA9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAB);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAD);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xAF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xB9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBB);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBD);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xBF);

        log.info(Arrays.toString(bytes));
    }

    private static final String BASIC_INST4 = "RET NZ\nPOP BC\nJP NZ,($0F0F0)\nJP (c0c0h)\nCALL NZ,($1368)\nPUSH BC\nADD A,12\n" +
            "RST 0H\nRET Z\nRET\nJP Z,($3333)\nCALL Z,($4444)\nCALL ($5555)\nADC A,14\nRST 8H\nRET NC\nPOP DE\nJP NC,($3333)\n" +
            "OUT (254),A\nCALL NC,(16)\nPUSH DE\nSUB A,66\nRST 10H\nRET C\nEXX\nJP C,($0099)\nIN A,(128)\nCALL C,(100)\nSBC A,feh\nRST 18H\n" +
            "RET PO\nPOP HL\nJP PO,(65535)\nEX (SP),HL\nCALL PO,(16385)\nPUSH HL\nAND A,1\nRST 20H\nRET PE\nJP (HL)\nJP PE,(0)\n" +
            "EX DE,HL\nCALL PE,(0x1111)\nXOR A,55\nRST 28H\n" +
            "RET P\nPOP AF\nJP P,(0b10101010)\nDI\nCALL P,(0255)\nPUSH AF\nOR A,0b111\nRST 30H\nRET M\nLD SP,HL\nJP M,(6000)\n" +
            "EI\nCALL M,(255)\nCP A,-1\nRST 38H";

    @Test
    void testBasicCommands4() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST4.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerNamespace namespace = new CompilerNamespace();
        CompilerApi compiler = CompilerFactory.create(namespace, new DefaultSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x68);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x13);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 12);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xC9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xCA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xCC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x44);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x44);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xCD);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x55);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x55);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xCE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 14);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xCF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x33);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 254);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 16);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 66);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xD9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xDA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x99);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xDB);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x80);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xDC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 100);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x00);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xDE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xDF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x01);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x40);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xE9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xEA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xEB);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xEC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x11);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0x11);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xEE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 55);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xEF);

        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF1);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF2);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0b10101010);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF3);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF4);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 173);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF5);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF6);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0b111);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF7);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF8);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xF9);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFA);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 112);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 23);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFB);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFC);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFE);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        Assertions.assertEquals(bytes[pc++] & 0xFF, 0xFF);
        log.info(Arrays.toString(bytes));
    }
}
