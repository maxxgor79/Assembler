package ru.assembler;

import ru.assembler.zxspectrum.basic.Lexem;
import ru.assembler.zxspectrum.basic.compile.Compiler;
import ru.assembler.zxspectrum.basic.decompile.Decompiler;

import java.util.Collection;

public class Test2 {
    public static void main(String[] args) {
        String text = "10 BORDER NOT PI\n 20 LOAD \"dd\" CODE 49123\n 30 RANDOMIZE USR 20000\n";
        Compiler compiler = new Compiler();
        Decompiler decompiler = new Decompiler();
        compiler.setData(text);
        try {
            final byte[] bytes = compiler.compile();
            System.out.println("Bytes.length=" + bytes.length);
            decompiler.setData(bytes);
            Collection<Lexem> lexems = decompiler.decompile();
            for (Lexem l : lexems) {
                System.out.print(l);
                System.out.print(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
