package ru.zxspectrum.disassembler.bytecode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author maxim
 * Date: 12/26/2023
 */
@Slf4j
@EqualsAndHashCode
public class ByteCodeUnit {
    private static final Map<Integer, ByteCodeUnit> INTEGER_MAP = new HashMap<>();

    static {
        for (int i = 0; i < 255; i++) {
            INTEGER_MAP.put(i, new ByteCodeUnit(i));
        }
    }

    private static final Map<String, ByteCodeUnit> PATTERN_MAP = new HashMap<>();

    static {
        for (Type t : Type.values()) {
            for (String pattern : t.getPatterns()) {
                try {
                    PATTERN_MAP.put(pattern, new ByteCodeUnit(pattern));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Getter
    private ByteCodeType type;

    @Getter
    private String value;

    public ByteCodeUnit(@NonNull ByteCodeType type, @NonNull String value) {
        this.type = type;
        setValue(value);
    }

    public ByteCodeUnit(int value) {
        this.value = SymbolUtils.toHexString(value);
        type = ByteCodeType.Code;
    }

    public ByteCodeUnit(@NonNull String pattern) {
        final Type type = Type.getByPattern(pattern);
        Objects.requireNonNull(type, pattern);
        this.type = ByteCodeType.Pattern;
        setValue(pattern);
    }

    protected void setValue(@NonNull String value) {
        if (type == ByteCodeType.Code) {
            if ((value.length() & 1) == 1) {
                throw new NumberFormatException("size is odd");
            }
            Integer.parseInt(value, 16);
        }
        this.value = value;
    }

    public static ByteCodeUnit valueOf(int value) {
        final ByteCodeUnit found = INTEGER_MAP.get(value);
        if (found != null) {
            return found;
        }
        return new ByteCodeUnit(value);//?
    }

    public static ByteCodeUnit valueOf(@NonNull String pattern) {
        final ByteCodeUnit unit = PATTERN_MAP.get(pattern);
        if (unit != null) {
            return unit;
        }
        try {
            return new ByteCodeUnit(pattern);//?
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Type getByteCodeType() {
        return switch (type) {
            case Code -> Type.getUnsignedBySize(value.length() / 2);
            case Pattern -> Type.getByPattern(value);
        };
    }

    public int getByteCodeSize() {
        return getByteCodeType().getSize();
    }

    @Override
    public String toString() {
        return value;
    }
}
