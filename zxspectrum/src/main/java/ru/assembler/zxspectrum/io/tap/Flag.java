package ru.assembler.zxspectrum.io.tap;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Maxim Gorin
 */
@ToString
@Getter
public enum Flag {
    Header(0), Data(255), Unknown(-1);

    Flag(int code) {
        this.code = code;
    }

    private final int code;

    public static Flag findByCode(int code) {
        for (Flag flag : values()) {
            if (flag.code == code) {
                return flag;
            }
        }
        return null;
    }
}
