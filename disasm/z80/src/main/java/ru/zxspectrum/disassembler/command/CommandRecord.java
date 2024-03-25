package ru.zxspectrum.disassembler.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
@EqualsAndHashCode
public class CommandRecord {
    @Getter
    private String codePattern;

    @Getter
    private String commandPattern;

    @Getter
    @NonNull
    private Behavior behavior = Behavior.Skip;

    @Getter
    @NonNull
    private String jumpAddressPattern;

    public CommandRecord(@NonNull String codePattern, @NonNull String commandPattern) {
        this.codePattern = codePattern;
        this.commandPattern = commandPattern;
    }

    public CommandRecord(@NonNull String codePattern, @NonNull String commandPattern, @NonNull Behavior behavior
            , String jumpAddressPattern) {
        this(codePattern, commandPattern);
        this.behavior = behavior;
        this.jumpAddressPattern = jumpAddressPattern;
    }
}
