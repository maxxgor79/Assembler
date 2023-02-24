package ru.zxspectrum.assembler.compiler.dummy;

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

    public NamespaceApiFreezed(NamespaceApi namespaceApi, BigInteger address, BigInteger currentCodeOffset) {
        if (namespaceApi == null) {
            throw new NullPointerException("namespaceApi");
        }
        this.namespaceApi = namespaceApi;
        if (address == null) {
            throw new NullPointerException("address");
        }
        this.address = address;
        if (currentCodeOffset == null) {
            throw new NullPointerException("currentCodeOffset");
        }
        this.currentCodeOffset = currentCodeOffset;
    }

    @Override
    public boolean containsLabel(String labelName) {
        return namespaceApi.containsLabel(labelName);
    }

    @Override
    public void putLabel(String name) {
        namespaceApi.putLabel(name);
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName, boolean used) {
        return namespaceApi.getLabelCodeOffset(labelName, used);
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName) {
        return namespaceApi.getLabelCodeOffset(labelName);
    }

    @Override
    public BigInteger getCurrentCodeOffset() {
        return currentCodeOffset;
    }

    @Override
    public BigInteger incCurrentCodeOffset(BigInteger delta) {
        return currentCodeOffset.add(delta);
    }

    @Override
    public void setAddress(BigInteger address) {

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
    public boolean isCompiled(File file) {
        return namespaceApi.isCompiled(file);
    }

    @Override
    public boolean addCompiled(File file) {
        return namespaceApi.addCompiled(file);
    }

    @Override
    public boolean containsVariable(String name) {
        return namespaceApi.containsVariable(name);
    }

    @Override
    public BigInteger getVariableValue(String name) {
        return namespaceApi.getVariableValue(name);
    }

    @Override
    public BigInteger addVariable(String name, BigInteger value) {
        return namespaceApi.addVariable(name, value);
    }
}
