package ru.zxspectrum.assembler;

import lombok.NonNull;
import ru.assembler.core.compiler.PostCommandCompiler;
import ru.assembler.core.ns.AbstractNamespaceApi;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */

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
    public BigInteger getMinAddress() {
        return address;
    }

    @Override
    public BigInteger getMaxAddress() {
        return address;
    }

    @Override
    public void addToQueue(@NonNull PostCommandCompiler commandCompiler) {

    }
}
