package ru.retro.assembler.editor.core.io;

import lombok.NonNull;

/**
 * @Author: Maxim Gorin
 * Date: 02.03.2024
 */
public class StoreFactory {
    public static Store getInstance(@NonNull StoreType type) {
        switch (type) {
            case MEMORY: return new MemoryStore();
        }
        return null;
    }
}
