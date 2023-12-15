package ru.zxspectrum.assembler;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.ns.AbstractNamespaceApi;

import java.math.BigInteger;

public class CompilerNamespace extends AbstractNamespaceApi {
    private final BigInteger address = new BigInteger("8000", 16);


    public CompilerNamespace() {

    }
    @Override
    public void setAddress(@NonNull BigInteger address) {

    }

    @Override
    public BigInteger getAddress() {
        return address;
    }

    @Override
    public void addToList(@NonNull PostCommandCompiler commandCompiler) {

    }
}
