package ru.assembler.core.ns;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.PostCommandCompiler;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
@Slf4j
public abstract class AbstractNamespaceApi implements NamespaceApi {
    protected final Map<String, LabelInfo> labelMap = new HashMap<>();

    protected final Map<BigInteger, String> addressMap = new HashMap<>();

    protected final Map<String, BigInteger> variableMap = new HashMap<>();

    protected final Set<File> compiledFileSet = new HashSet<>();

    protected BigInteger currentCodeOffset = BigInteger.ZERO;

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
    public void putLabel(@NonNull final String name) {
        putLabel(name, currentCodeOffset);
    }

    public void putLabel(@NonNull final String name, @NonNull final BigInteger address) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        labelMap.put(name, new LabelInfo(address));
        addressMap.put(address, name);
    }

    @Override
    public String getLabel(@NonNull BigInteger address) {
        return addressMap.get(address);
    }


    @Override
    public BigInteger getLabelCodeOffset(final String labelName, final boolean used) {
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
            return null;
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
        currentCodeOffset = currentCodeOffset.add(delta);
        return currentCodeOffset;
    }

    @Override
    public abstract void setAddress(@NonNull BigInteger address);

    @Override
    public abstract BigInteger getAddress();

    @Override
    public abstract void addToQueue(@NonNull PostCommandCompiler commandCompiler);

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
        if (name.trim().isEmpty()) {
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
