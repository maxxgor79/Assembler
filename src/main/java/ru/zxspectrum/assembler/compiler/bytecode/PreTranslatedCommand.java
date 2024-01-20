package ru.zxspectrum.assembler.compiler.bytecode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.error.AssemblerException;
import ru.zxspectrum.assembler.lang.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Maxim Gorin
 */
@EqualsAndHashCode
@ToString
@Slf4j
public class PreTranslatedCommand {
    private final Object[] data;

    @Getter
    private final int size;

    @Getter
    private final ByteOrder byteOrder;

    public PreTranslatedCommand(@NonNull Object[] data, ByteOrder byteOrder) {
        this.data = data;
        this.byteOrder = byteOrder;
        size = calcSize();
    }

    public PreTranslatedCommand(@NonNull List<Object> list, ByteOrder byteOrder) {
        this.data = list.toArray();
        this.byteOrder = (byteOrder == null) ? ByteOrder.LittleEndian : byteOrder;
        size = calcSize();
    }

    private int calcSize() {
        int size = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof byte[]) {
                size += ((byte[]) data[i]).length;
            } else if (data[i] instanceof Type) {
                size += ((Type) data[i]).getSize();
            }
        }
        return size;
    }

    public byte[] encode(@NonNull BigInteger... arg) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        int argIndex = 0;
        try {
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof byte[]) {
                    os.write((byte[]) data[i]);
                } else if (data[i] instanceof Type) {
                    if (argIndex >= arg.length) {
                        throw new AssemblerException("Expected argument with index=" + argIndex);
                    }
                    final Type type = (Type) data[i];
                    os.write(type.getBytes(arg[argIndex++], byteOrder));
                } else throw new AssemblerException("Bad byte code: " + this);
            }
            return os.toByteArray();
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            throw new AssemblerException(e.getMessage());
        }
    }

    public int getOffset(int argIndex) {
        int curArgIndex = 0;
        int offset = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof byte[]) {
                offset += ((byte[]) data[i]).length;
            } else if (data[i] instanceof Type) {
                Type type = (Type) data[i];
                offset += type.getSize();
                if (curArgIndex == argIndex) {
                    return offset;
                }
                curArgIndex++;
            }
        }
        throw new IndexOutOfBoundsException("index=" + argIndex);
    }
}
