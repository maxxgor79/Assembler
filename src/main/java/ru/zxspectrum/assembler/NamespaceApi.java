package ru.zxspectrum.assembler;

import ru.zxspectrum.assembler.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 *
 */
public interface NamespaceApi {
    public boolean containsLabel(String labelName);

    public void putLabel(String name);

    public BigInteger getLabelCodeOffset(String labelName, boolean used);

    public BigInteger getLabelCodeOffset(String labelName);

    public BigInteger getCurrentCodeOffset();

    public BigInteger incCurrentCodeOffset(BigInteger delta);

    public void setAddress(BigInteger address);

    public BigInteger getAddress();

    public void addToList(PostCommandCompiler commandCompiler);

    public boolean isCompiled(File file);

    public boolean addCompiled(File file);

    public boolean containsVariable(String name);

    public BigInteger getVariableValue(String name);

    public BigInteger addVariable(String name, BigInteger value);
}
