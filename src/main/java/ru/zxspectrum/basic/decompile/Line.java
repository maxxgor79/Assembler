package ru.zxspectrum.basic.decompile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Line {
    @Getter
    @Setter
    private int lineNumber;

    @Getter
    @Setter
    private byte [] lineData;
}
