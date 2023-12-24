package ru.zxspectrum.assembler.compiler.option;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Option {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private OptionType type;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private Object content;

    public Option(@NonNull OptionType type) {
        setType(type);
    }

    public Option(@NonNull OptionType type, @NonNull Object content) {
        setType(type);
        setContent(content);
    }
}
