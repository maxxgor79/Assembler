package ru.zxspectrum.disassembler.render.command;

import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnits;
import ru.zxspectrum.disassembler.bytecode.CodePatternParser;
import ru.zxspectrum.disassembler.command.Behavior;
import ru.zxspectrum.disassembler.command.CommandRecord;
import ru.zxspectrum.disassembler.lexem.LexemParser;
import ru.zxspectrum.disassembler.lexem.Lexemes;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author maxim
 * Date: 12/27/2023
 */
public class CommandFactory {
    private static final LexemParser LEXEME_PARSER = new LexemParser();

    private static final CodePatternParser CODE_PATTERN_PARSER = new CodePatternParser();

    @Getter
    private final ByteCodeUnits byteCodeUnits = new ByteCodeUnits();

    @Getter
    private final Lexemes lexemes = new Lexemes();

    @Getter
    private Behavior behavior;

    @Getter
    private String jumpAddressPattern;

    public CommandFactory(@NonNull final CommandRecord cr) throws IOException {
        LEXEME_PARSER.setInputStream(new ByteArrayInputStream(cr.getCommandPattern().getBytes()));
        lexemes.add(LEXEME_PARSER.parse());
        CODE_PATTERN_PARSER.setCodePattern(cr.getCodePattern());
        byteCodeUnits.add(CODE_PATTERN_PARSER.parse());
        behavior = cr.getBehavior();
        jumpAddressPattern = cr.getJumpAddressPattern();
    }

    public Instruction create() {
        return new Instruction(byteCodeUnits, lexemes, behavior, jumpAddressPattern);
    }
}
