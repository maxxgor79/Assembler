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
    private static final String BASIC_INST = "nop\nld bc,10h\n";

    @Test
    void testBasicCommands() throws IOException {
        ResourceSettings resourceSettings = new ResourceSettings();
        resourceSettings.load("settings.properties");
        ByteArrayInputStream bis = new ByteArrayInputStream(BASIC_INST.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        CompilerApi compiler = CompilerFactory.create(new CompilerNamespace(), new ConstantSettings()
                , new File("test"), bis, bos);
        compiler.compile();
        byte[] bytes = bos.toByteArray();
        int pc = 0;
        Assertions.assertEquals(bytes[pc++], 0);
        Assertions.assertEquals(bytes[pc++], 1);
        Assertions.assertEquals(bytes[pc++], 0x10);
        Assertions.assertEquals(bytes[pc++], 0);
        log.info(Arrays.toString(bytes));
    }

    @Test
    void test2() {

    }
}
