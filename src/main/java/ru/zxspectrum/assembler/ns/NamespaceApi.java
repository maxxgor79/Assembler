package ru.zxspectrum.assembler.ns;

import ru.zxspectrum.assembler.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public interface NamespaceApi {
    void reset();
    boolean containsLabel(String labelName);

    void putLabel(String name);

    BigInteger getLabelCodeOffset(String labelName, boolean used);

    BigInteger getLabelCodeOffset(String labelName);

    BigInteger getCurrentCodeOffset();

    BigInteger incCurrentCodeOffset(BigInteger delta);

    void setAddress(BigInteger address);

    BigInteger getAddress();

    void addToList(PostCommandCompiler commandCompiler);

    boolean isCompiled(File file);

    boolean addCompiled(File file);

    boolean containsVariable(String name);

    BigInteger getVariableValue(String name);

    BigInteger addVariable(String name, BigInteger value);

    boolean removeVariable(String name);
}
