package ru.zxspectrum.disassembler.sys;

import ru.zxspectrum.disassembler.settings.DisassemblerSettings;

import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public interface Environment {
    String getExtension();

    int getAddressSize();

    DisassemblerSettings getSettings();

    String getLabel(BigInteger address);

    void putLabel(BigInteger address, String labelName);
}
