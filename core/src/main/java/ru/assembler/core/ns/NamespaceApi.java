package ru.assembler.core.ns;

import ru.assembler.core.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
public interface NamespaceApi {
    void reset();

    boolean containsLabel(String labelName);

    void putLabel(String name);

    void putLabel(String name, BigInteger address);

    String getLabel(BigInteger address);

    BigInteger getLabelCodeOffset(String labelName, boolean used);

    BigInteger getLabelCodeOffset(String labelName);

    BigInteger getCurrentCodeOffset();

    BigInteger incCurrentCodeOffset(BigInteger delta);

    void setAddress(BigInteger address);

    BigInteger getAddress();

    void addToQueue(PostCommandCompiler commandCompiler);

    boolean isCompiled(File file);

    boolean addCompiled(File file);

    boolean containsVariable(String name);

    BigInteger getVariableValue(String name);

    BigInteger addVariable(String name, BigInteger value);

    boolean removeVariable(String name);
}
