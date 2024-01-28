package ru.assembler.zxspectrum.core.compiler.option;

public final class OptionTypes {
    private OptionTypes() {

    }

    public static final ProduceWavOptionType PRODUCE_WAV = new ProduceWavOptionType();

    public static final ProduceTapOptionType PRODUCE_TAP = new ProduceTapOptionType();

    public static final ProduceTzxOptionType PRODUCE_TZX = new ProduceTzxOptionType();
}
