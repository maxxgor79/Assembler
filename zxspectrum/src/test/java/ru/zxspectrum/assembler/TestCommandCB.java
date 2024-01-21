package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.settings.DefaultSettings;
import ru.assembler.core.settings.ResourceSettings;
import ru.assembler.zxspectrum.core.compiler.Z80Compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCommandCB {
    private static final String INST1 = "RLC B\nRLC C\nRLC D\nRLC E\nRLC H\nRLC L\nRLC (HL)\nRLC A\nRRC B\nRRC C\nRRC D\n" +
            "RRC E\nRRC H\nRRC L\nRRC (HL)\nRRC A\nRL B\nRL C\nRL D\nRL E\nRL H\nRL L\nRL (HL)\nRL A\nRR B\nRR C\nRR D\nRR E\n" +
            "RR H\nRR L\nRR (HL)\nRR A\n";

    @Test
    void testCbCommands1() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, cmd);
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST2 = "SLA B\nSLA C\nSLA D\nSLA E\nSLA H\nSLA L\nSLA (HL)\nSLA A\nSRA B\nSRA C\nSRA D\nSRA E\nSRA H\nSRA L\nSRA (HL)\nSRA A\n";

    @Test
    void testCbCommands2() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0x20 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST3 = "SRL B\nSRL C\nSRL D\nSRL E\nSRL H\nSRL L\nSRL (HL)\nSRL A\n" +
            "BIT 0,B\nBIT 0,C\nBIT 0,D\nBIT 0,E\nBIT 0,H\nBIT 0,L\nBIT 0,(HL)\nBIT 0,A\nBIT 1,B\nBIT 1,C\nBIT 1,D\n" +
            "BIT 1,E\nBIT 1,H\nBIT 1,L\nBIT 1,(HL)\nBIT 1,A\n";

    @Test
    void testCbCommands3() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0x38 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST4 = "BIT 2,B\nBIT 2,C\nBIT 2,D\nBIT 2,E\nBIT 2,H\nBIT 2,L\nBIT 2,(HL)\nBIT 2,A\n" +
            "BIT 3,B\nBIT 3,C\nBIT 3,D\nBIT 3,E\nBIT 3,H\nBIT 3,L\nBIT 3,(HL)\nBIT 3,A\n" +
            "BIT 4,B\nBIT 4,C\nBIT 4,D\nBIT 4,E\nBIT 4,H\nBIT 4,L\nBIT 4,(HL)\nBIT 4,A\nBIT 5,B\nBIT 5,C\nBIT 5,D\n" +
            "BIT 5,E\nBIT 5,H\nBIT 5,L\nBIT 5,(HL)\nBIT 5,A\n" +
            "BIT 6,B\nBIT 6,C\nBIT 6,D\nBIT 6,E\nBIT 6,H\nBIT 6,L\nBIT 6,(HL)\nBIT 6,A\nBIT 7,B\nBIT 7,C\nBIT 7,D\n" +
            "BIT 7,E\nBIT 7,H\nBIT 7,L\nBIT 7,(HL)\nBIT 7,A\n";

    @Test
    void testCbCommands4() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0x50 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST5 = "RES 0,B\nRES 0,C\nRES 0,D\nRES 0,E\nRES 0,H\nRES 0,L\nRES 0,(HL)\nRES 0,A\n" +
            "RES 1,B\nRES 1,C\nRES 1,D\nRES 1,E\nRES 1,H\nRES 1,L\nRES 1,(HL)\nRES 1,A\n" +
            "RES 2,B\nRES 2,C\nRES 2,D\nRES 2,E\nRES 2,H\nRES 2,L\nRES 2,(HL)\nRES 2,A\nRES 3,B\nRES 3,C\nRES 3,D\n" +
            "RES 3,E\nRES 3,H\nRES 3,L\nRES 3,(HL)\nRES 3,A\n" +
            "RES 4,B\nRES 4,C\nRES 4,D\nRES 4,E\nRES 4,H\nRES 4,L\nRES 4,(HL)\nRES 4,A\nRES 5,B\nRES 5,C\nRES 5,D\n" +
            "RES 5,E\nRES 5,H\nRES 5,L\nRES 5,(HL)\nRES 5,A";

    @Test
    void testCbCommands5() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0x80 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST6 = "RES 6,B\nRES 6,C\nRES 6,D\nRES 6,E\nRES 6,H\nRES 6,L\nRES 6,(HL)\nRES 6,A\n" +
            "RES 7,B\nRES 7,C\nRES 7,D\nRES 7,E\nRES 7,H\nRES 7,L\nRES 7,(HL)\nRES 7,A\n" +
            "SET 0,B\nSET 0,C\nSET 0,D\nSET 0,E\nSET 0,H\nSET 0,L\nSET 0,(HL)\nSET 0,A\nSET 1,B\nSET 1,C\nSET 1,D\n" +
            "SET 1,E\nSET 1,H\nSET 1,L\nSET 1,(HL)\nSET 1,A\n" +
            "SET 2,B\nSET 2,C\nSET 2,D\nSET 2,E\nSET 2,H\nSET 2,L\nSET 2,(HL)\nSET 2,A\nSET 3,B\nSET 3,C\nSET 3,D\n" +
            "SET 3,E\nSET 3,H\nSET 3,L\nSET 3,(HL)\nSET 3,A";

    @Test
    void testCbCommands6() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0xB0 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }

    private static final String INST7 = "SET 4,B\nSET 4,C\nSET 4,D\nSET 4,E\nSET 4,H\nSET 4,L\nSET 4,(HL)\nSET 4,A\n" +
            "SET 5,B\nSET 5,C\nSET 5,D\nSET 5,E\nSET 5,H\nSET 5,L\nSET 5,(HL)\nSET 5,A\n" +
            "SET 6,B\nSET 6,C\nSET 6,D\nSET 6,E\nSET 6,H\nSET 6,L\nSET 6,(HL)\nSET 6,A\nSET 7,B\nSET 7,C\nSET 7,D\n" +
            "SET 7,E\nSET 7,H\nSET 7,L\nSET 7,(HL)\nSET 7,A";

    @Test
    void testCbCommands7() throws IOException {
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

        for (int i = 0, cmd = 0; i < (bytes.length / 2); i += 2, cmd++) {
            Assertions.assertEquals(bytes[pc++] & 0xFF, 0xcb);
            Assertions.assertEquals(bytes[pc++] & 0xFF, (0xE0 + cmd));
        }
        log.info(Arrays.toString(bytes));
    }
}
