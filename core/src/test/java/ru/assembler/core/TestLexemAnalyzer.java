package ru.assembler.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemAnalyzer;
import ru.assembler.core.lexem.LexemType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Maxim Gorin
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestLexemAnalyzer {
    @Test
    public void testNumbers() throws IOException {
        String numbers = "1234 0h 0123 0x123 0b111 0123h 1001b 077q 0";
        LexemAnalyzer analyzer = new LexemAnalyzer(new File(""), new ByteArrayInputStream(numbers.getBytes()));
        Lexem lexem;
        Iterator<Lexem> iter = analyzer.iterator();
        List<Lexem> lexemList = new ArrayList<>();
        while(iter.hasNext()) {
            lexem = iter.next();
            lexemList.add(lexem);
        }
        Assertions.assertEquals(lexemList.get(0), new Lexem( LexemType.DECIMAL, "1234"));
        Assertions.assertEquals(lexemList.get(1), new Lexem( LexemType.HEXADECIMAL, "0"));
        Assertions.assertEquals(lexemList.get(2), new Lexem( LexemType.OCTAL, "123"));
        Assertions.assertEquals(lexemList.get(3), new Lexem( LexemType.HEXADECIMAL, "123"));
        Assertions.assertEquals(lexemList.get(4), new Lexem( LexemType.BINARY, "111"));
        Assertions.assertEquals(lexemList.get(5), new Lexem( LexemType.HEXADECIMAL, "123"));
        Assertions.assertEquals(lexemList.get(6), new Lexem( LexemType.BINARY, "1001"));
        Assertions.assertEquals(lexemList.get(7), new Lexem( LexemType.OCTAL, "77"));
        Assertions.assertEquals(lexemList.get(8), new Lexem( LexemType.OCTAL, "0"));
    }

    @Test
    public void testLexemAnalyzer() {
        String asm = ".img \"test.png\"\n";
        LexemAnalyzer analyzer = new LexemAnalyzer(new ByteArrayInputStream(asm.getBytes()));
        for (Lexem lexem : analyzer) {
            System.out.println("Lexem: " + lexem);
        }
    }
}
