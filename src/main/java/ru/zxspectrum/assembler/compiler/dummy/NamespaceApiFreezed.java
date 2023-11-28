package ru.zxspectrum.assembler.compiler.dummy;

import lombok.NonNull;
import ru.zxspectrum.assembler.NamespaceApi;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public class NamespaceApiFreezed implements NamespaceApi {
    private NamespaceApi namespaceApi;

    private BigInteger address = BigInteger.ZERO;

    private BigInteger currentCodeOffset = BigInteger.ZERO;

    public NamespaceApiFreezed(NamespaceApi namespaceApi) {
        this(namespaceApi, namespaceApi.getAddress(), namespaceApi.getCurrentCodeOffset());
    }

    public NamespaceApiFreezed(@NonNull NamespaceApi namespaceApi, @NonNull BigInteger address
            , @NonNull BigInteger currentCodeOffset) {
        this.namespaceApi = namespaceApi;
        this.address = address;
        this.currentCodeOffset = currentCodeOffset;
    }

    @Override
    public boolean containsLabel(String labelName) {
        return namespaceApi.containsLabel(labelName);
    }

    @Override
    public void putLabel(@NonNull String name) {
        namespaceApi.putLabel(name);
    }

    @Override
    public BigInteger getLabelCodeOffset(@NonNull String labelName, boolean used) {
        return namespaceApi.getLabelCodeOffset(labelName, used);
    }

    @Override
    public BigInteger getLabelCodeOffset(@NonNull String labelName) {
        return namespaceApi.getLabelCodeOffset(labelName);
    }

    @Override
    public BigInteger getCurrentCodeOffset() {
        return currentCodeOffset;
    }

    @Override
    public BigInteger incCurrentCodeOffset(@NonNull BigInteger delta) {
        return currentCodeOffset.add(delta);
    }

    @Override
    public void setAddress(@NonNull BigInteger address) {

    }

    @Override
    public BigInteger getAddress() {
        return address;
    }

    @Override
    public void addToList(PostCommandCompiler commandCompiler) {
        namespaceApi.addToList(commandCompiler);
    }

    @Override
    public boolean isCompiled(@NonNull File file) {
        return namespaceApi.isCompiled(file);
    }

    @Override
    public boolean addCompiled(@NonNull File file) {
        return namespaceApi.addCompiled(file);
    }

    @Override
    public boolean containsVariable(@NonNull String name) {
        return namespaceApi.containsVariable(name);
    }

    @Override
    public BigInteger getVariableValue(@NonNull String name) {
        return namespaceApi.getVariableValue(name);
    }

    @Override
    public BigInteger addVariable(@NonNull String name, @NonNull BigInteger value) {
        return namespaceApi.addVariable(name, value);
    }
}
