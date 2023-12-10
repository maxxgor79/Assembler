package ru.zxspectrum.assembler;

import lombok.Data;
import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Encoding;

import java.io.File;
import java.math.BigInteger;

@Data
public class Args {
    @NonNull
    private Encoding sourceEncoding = Encoding.UTF_8;

    @NonNull
    private Encoding platformEncoding = Encoding.ASCII;

    @NonNull
    private ByteOrder byteOrder = ByteOrder.LittleEndian;

    @NonNull
    private BigInteger address = BigInteger.ZERO;

    @NonNull
    private BigInteger minAddress = BigInteger.ZERO;

    @NonNull
    private BigInteger maxAddress = new BigInteger("FFFF", 16);

    @NonNull
    private File outputDirectory = new File("output");

    @NonNull
    private boolean produceTap = false;

    @NonNull
    private boolean produceWav = false;

    @NonNull
    private String majorVersion = "1";

    @NonNull
    private String minorVersion = "1";
}
