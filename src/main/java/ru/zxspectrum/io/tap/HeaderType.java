package ru.zxspectrum.io.tap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public enum HeaderType {
    Program(0), NumberArray(1), CharArray(2), Bytes(3), Code(3);

    HeaderType(int code) {
        this.code = code;
    }

    @Getter
    @Setter
    private final int code;

    public static HeaderType getByCode(int code) {
        for (HeaderType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
