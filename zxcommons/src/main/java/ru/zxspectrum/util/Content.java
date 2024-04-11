package ru.zxspectrum.util;

import lombok.*;

import java.math.BigInteger;

/**
 * Author: Maxim Gorin
 * Date: 11.04.2024
 */
@ToString
@AllArgsConstructor
public class Content {
    @Setter
    @Getter
    @NonNull
    private ContentType contentType;

    @Getter
    @Setter
    private BigInteger startAddress;

    @Getter
    @Setter
    @NonNull
    private byte [] bytes;
}
