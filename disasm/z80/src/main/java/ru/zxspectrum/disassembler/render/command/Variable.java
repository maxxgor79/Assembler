package ru.zxspectrum.disassembler.render.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.render.Number;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 12/27/2023
 */
@EqualsAndHashCode
public class Variable extends Number {
    @Getter
    private static int radix = 16;

    @Getter
    @Setter
    @NonNull
    private String name;

    @Getter
    @Setter
    @NonNull
    private Type type;

    public Variable(long value, @NonNull Type type) {
        this(BigInteger.valueOf(value), type);
    }

    public Variable(@NonNull BigInteger value, @NonNull Type type) {
        setValue(value);
    }

    public Variable(@NonNull String name, @NonNull BigInteger value, @NonNull Type type) {
        this(value, type);
        setName(name);
    }

    @Override
    public String generate() throws RenderException {
        return toString();
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return generateValue();
    }
}
