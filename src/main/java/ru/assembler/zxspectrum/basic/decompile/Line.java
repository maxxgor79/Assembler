package ru.assembler.zxspectrum.basic.decompile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Line {
    private int lineNumber;

    private byte [] lineData;
}
