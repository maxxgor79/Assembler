package ru.assembler.microsha;

import lombok.NonNull;
import ru.assembler.core.compiler.PostCommandCompiler;
import ru.assembler.core.ns.AbstractNamespaceApi;

import java.math.BigInteger;

public class MicroshaAssembler extends AbstractNamespaceApi {
    @Override
    public void setAddress(@NonNull BigInteger address) {

    }

    @Override
    public BigInteger getAddress() {
        return null;
    }

    @Override
    public void addToQueue(@NonNull PostCommandCompiler commandCompiler) {

    }
}
