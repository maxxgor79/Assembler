package ru.zxspectrum.assembler.ns;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractNamespaceApi implements NamespaceApi {
    protected final Map<String, LabelInfo> labelMap = new HashMap<>();

    protected final Map<String, BigInteger> variableMap = new HashMap<>();

    protected final Set<File> compiledFileSet = new HashSet<>();

    protected BigInteger currentCodeOffset;

    @NonNull
    public void reset() {
        currentCodeOffset = BigInteger.ZERO;
        variableMap.clear();
        labelMap.clear();
    }

    @Override
    public boolean containsLabel(String labelName) {
        return labelMap.containsKey(labelName);
    }

    @Override
    public void putLabel(@NonNull String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        labelMap.put(name, new LabelInfo(currentCodeOffset));
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName, boolean used) {
        LabelInfo labelInfo = labelMap.get(labelName);
        if (labelInfo == null) {
            return BigInteger.valueOf(-1);
        }
        labelInfo.setUsed(used);
        return labelInfo.getCodeOffset();
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName) {
        LabelInfo labelInfo = labelMap.get(labelName);
        if (labelInfo == null) {
            return BigInteger.valueOf(-1);
        }
        return labelInfo.getCodeOffset();
    }

    @Override
    public BigInteger getCurrentCodeOffset() {
        return currentCodeOffset;
    }

    @Override
    public BigInteger incCurrentCodeOffset(@NonNull BigInteger delta) {
        if (delta.signum() == -1) {
            throw new IllegalArgumentException("delta is negative");
        }
        if (delta == null) {
            throw new NullPointerException("delta");
        }
        currentCodeOffset = currentCodeOffset.add(delta);
        return currentCodeOffset;
    }

    @Override
    public abstract void setAddress(@NonNull BigInteger address);

    @Override
    public abstract BigInteger getAddress();

    @Override
    public abstract void addToList(@NonNull PostCommandCompiler commandCompiler);

    @Override
    public boolean isCompiled(File file) {
        if (file == null) {
            return false;
        }
        return compiledFileSet.contains(file);
    }

    @Override
    public boolean addCompiled(@NonNull File file) {
        return compiledFileSet.add(file);
    }

    @Override
    public boolean containsVariable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return variableMap.containsKey(name);
    }

    @Override
    public BigInteger getVariableValue(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return variableMap.get(name);
    }

    @Override
    public BigInteger addVariable(@NonNull String name, @NonNull BigInteger value) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        if (value == null) {
            return null;
        }
        return variableMap.put(name, value);
    }

    @Override
    public boolean removeVariable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return variableMap.remove(name) != null;
    }

    static class LabelInfo {

        @Getter
        BigInteger codeOffset;

        @Getter
        @Setter
        boolean used;

        LabelInfo(@NonNull BigInteger codeOffset) {
            this.codeOffset = codeOffset;
        }
    }
}
