package ru.zxspectrum.disassembler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zxspectrum.disassembler.bytecode.ByteCodeType;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnits;
import ru.zxspectrum.disassembler.bytecode.CodePatternParser;

import java.util.Arrays;
import java.util.List;

/**
 * @author maxim
 * Date: 12/26/2023
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestCodePatternParser {

    @Test
    public void testCodePattern1() {
        final String pattern1 = "8090$nn00";
        List<ByteCodeUnit> originList = Arrays.asList(new ByteCodeUnit(ByteCodeType.Code, "80")
                , new ByteCodeUnit(ByteCodeType.Code, "90")
                , new ByteCodeUnit(ByteCodeType.Pattern, "nn")
                , new ByteCodeUnit(ByteCodeType.Code, "00"));
        final CodePatternParser parser = new CodePatternParser();
        parser.setCodePattern(pattern1);
        ByteCodeUnits units = parser.parse();
        Assertions.assertArrayEquals(units.getUnits().toArray(new ByteCodeUnit[units.count()])
                , originList.toArray(new ByteCodeUnit[originList.size()]));

        final String pattern2 = "00$nnnn$e01";
        originList = Arrays.asList(new ByteCodeUnit(ByteCodeType.Code, "00")
                , new ByteCodeUnit(ByteCodeType.Pattern, "nnnn")
                , new ByteCodeUnit(ByteCodeType.Pattern, "e")
                , new ByteCodeUnit(ByteCodeType.Code, "01"));
        parser.setCodePattern(pattern2);
        units = parser.parse();
        Assertions.assertArrayEquals(units.getUnits().toArray(new ByteCodeUnit[units.count()])
                , originList.toArray(new ByteCodeUnit[originList.size()]));
    }

    @Test
    public void testCodePattern2() {
        final String pattern1 = "90$d00$dd";
        List<ByteCodeUnit> originList = Arrays.asList(new ByteCodeUnit(ByteCodeType.Code, "90")
                , new ByteCodeUnit(ByteCodeType.Pattern, "d")
                , new ByteCodeUnit(ByteCodeType.Code, "00")
                , new ByteCodeUnit(ByteCodeType.Pattern, "dd"));
        final CodePatternParser parser = new CodePatternParser();
        parser.setCodePattern(pattern1);
        ByteCodeUnits units = parser.parse();
        Assertions.assertArrayEquals(units.getUnits().toArray(new ByteCodeUnit[units.count()])
                , originList.toArray(new ByteCodeUnit[originList.size()]));

        final String pattern2 = "ee$ee$nFf";
        originList = Arrays.asList(new ByteCodeUnit(ByteCodeType.Code, "ee")
                , new ByteCodeUnit(ByteCodeType.Pattern, "ee")
                , new ByteCodeUnit(ByteCodeType.Pattern, "n")
                , new ByteCodeUnit(ByteCodeType.Code, "Ff"));
        parser.setCodePattern(pattern2);
        units = parser.parse();
        Assertions.assertArrayEquals(units.getUnits().toArray(new ByteCodeUnit[units.count()])
                , originList.toArray(new ByteCodeUnit[originList.size()]));
    }
}
