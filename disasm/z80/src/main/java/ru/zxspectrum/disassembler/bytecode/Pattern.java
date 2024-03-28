package ru.zxspectrum.disassembler.bytecode;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @Author: Maxim Gorin
 * Date: 28.03.2024
 */
public class Pattern {
    @Getter
    @Setter
    @NonNull
    private String value;

    public Pattern(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is empty");
        }
        this.value = value;
    }

    public Pattern(@NonNull ByteCodeUnit unit) {
        if (unit.getType() != ByteCodeType.Pattern) {
            throw new IllegalArgumentException("No pattern: " + unit.getValue());
        }
        if (unit.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("value is empty");
        }
        this.value = unit.getValue();
    }

    public int getDimension() {
        return value.length();
    }

    public boolean isNumber() {
        return value.toLowerCase().startsWith("n");
    }

    public boolean isAddressOffset() {
        return value.toLowerCase().startsWith("e");
    }

    public boolean isOffset() {
        return value.toLowerCase().startsWith("d");
    }
}
