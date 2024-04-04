package ru.zxspectrum.disassembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.command.CommandRecord;
import ru.zxspectrum.disassembler.io.ByteCodeInputStream;
import ru.zxspectrum.disassembler.lang.tree.Navigator;
import ru.zxspectrum.disassembler.lang.tree.Tree;
import ru.zxspectrum.disassembler.loader.DisassemblerLoader;
import ru.zxspectrum.disassembler.render.command.CommandFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author maxim
 * Date: 1/3/2024
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestIO {

    @Test
    public void test1() throws IOException {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        ByteCodeInputStream bcis = new ByteCodeInputStream(data);
        Assertions.assertEquals(bcis.read(), 1);
        Assertions.assertEquals(bcis.read(), 2);
        bcis.pushback();
        Assertions.assertEquals(bcis.read(), 2);
    }

    @Test
    public void test2() throws IOException {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        ByteCodeInputStream bcis = new ByteCodeInputStream(data);
        Assertions.assertEquals(bcis.read(), 1);
        Assertions.assertEquals(bcis.read(), 2);
        Assertions.assertEquals(bcis.read(), 3);
        Assertions.assertEquals(bcis.read(), 4);
        Assertions.assertEquals(bcis.read(), 5);
        Assertions.assertEquals(bcis.read(), 6);
        Assertions.assertEquals(bcis.read(), -1);
    }

    @Test
    public void test3() throws IOException {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        ByteCodeInputStream bcis = new ByteCodeInputStream(data);
        bcis.jump(5);
        Assertions.assertEquals(bcis.read(), 6);
        bcis.jump(1);
        Assertions.assertEquals(bcis.read(), 2);
    }

    @Test
    public void test4() throws IOException {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        ByteCodeInputStream bcis = new ByteCodeInputStream(data);
        bcis.moveTo(5);
        Assertions.assertEquals(bcis.read(), 6);
        bcis.moveTo(-2);
        Assertions.assertEquals(bcis.read(), 5);
    }

    @Test
    public void test5() throws IOException {
        byte[] data = new byte[]{0, 1, 0, 3, 0, 0x40, -1, (byte) 255, 0, 0, 0, 1};
        ByteCodeInputStream bcis = new ByteCodeInputStream(data);
        Assertions.assertEquals(bcis.readShort(), 256);
        Assertions.assertEquals(bcis.readUnsignedShort(), 768);
        Assertions.assertEquals(bcis.readUnsignedShort(), 0x4000);
        Assertions.assertEquals(bcis.read(), 255);
        Assertions.assertEquals(bcis.readByte(), -1);
        Assertions.assertEquals(bcis.readInt(), 0x0100_0000);
    }

    @Test
    public void test6() throws IOException {
        final DisassemblerLoader loader = new DisassemblerLoader();
        final Set<CommandRecord> commands = new HashSet<>();
        for (String path : Arrays.asList("/disasm/template/z80/basic.op", "/disasm/template/z80/extended.op"
                , "/disasm/template/z80/bitwise.op", "/disasm/template/z80/ix.op", "/disasm/template/z80/iy.op", "/disasm/template/z80/undocumented.op")) {
            final InputStream is = Disassembler.class.getResourceAsStream(path);
            if (is != null) {
                commands.addAll(loader.load(is, Charset.defaultCharset()));
            }
            is.close();
        }
        Tree<CommandFactory> tree = new Tree<>();
        for (final CommandRecord r : commands) {
            tree.add(r.getCodePattern(), new CommandFactory(r));
        }

        Navigator<CommandFactory> navigator = tree.getNavigator();
        for (int i = 0; i < 255; i++) {
            Assertions.assertTrue(navigator.contains(ByteCodeUnit.valueOf(i)));
        }
     }
}
