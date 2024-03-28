package ru.zxspectrum.disassembler.render.system;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.render.Address;
import ru.zxspectrum.disassembler.render.command.Command;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/4/2024
 */
@EqualsAndHashCode
@ToString
public class Org extends Command {
    protected static final String NAME = "ORG";

    private final Address address;

    public Org(@NonNull final BigInteger address) {
        this.address = new Address(address) {
            @Override
            public String generate() throws RenderException {
                return generateValue();
            }
        };
    }

    @Override
    public String generate() throws RenderException {
        return new StringBuilder().append(toUpperOrLowerCase(NAME)).append(SPACE)
                .append(address.generate()).toString();
    }
}
