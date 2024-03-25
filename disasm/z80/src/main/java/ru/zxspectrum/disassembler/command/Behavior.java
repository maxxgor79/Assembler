package ru.zxspectrum.disassembler.command;

import lombok.Getter;
import lombok.NonNull;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public enum Behavior {
    Skip(null), Stop("s"), Jump("j"), ConditionalJump("cj");

    Behavior(String abbreviature) {
        this.abbreviature = abbreviature;
    }

    @Getter
    private String abbreviature;

    public static Behavior getBehavior(String abbreviature) {
        if (abbreviature == null) {
            return Skip;
        }
        for (Behavior b : values()) {
            if (abbreviature.equals(b.abbreviature)) {
                return b;
            }
        }
        return null;
    }

    public static boolean isJumpGroup(@NonNull Behavior behavior) {
        return behavior == Jump || behavior == ConditionalJump;
    }
}
