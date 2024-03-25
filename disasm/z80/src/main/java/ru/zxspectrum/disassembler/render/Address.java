package ru.zxspectrum.disassembler.render;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/3/2024
 */
@EqualsAndHashCode
public class Address extends Number {
    @Getter
    @Setter
    private boolean visible = true;

    public Address() {
    }

    public Address(@NonNull BigInteger address) {
        this();
        this.value = address;
    }

    public Address(@NonNull BigInteger address, boolean visible) {
        this();
        this.value = address;
        this.visible = visible;
    }

    @Override
    public String generate() throws RenderException {
        return visible ? generateValue() : "";
    }
}
