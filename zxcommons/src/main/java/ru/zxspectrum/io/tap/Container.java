package ru.zxspectrum.io.tap;

/**
 * Author: Maxim Gorin
 * Date: 11.04.2024
 */
public interface Container {
    byte[] getContent();

    void setContent(byte [] data);
}
