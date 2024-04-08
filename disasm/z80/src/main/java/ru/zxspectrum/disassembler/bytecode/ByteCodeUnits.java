package ru.zxspectrum.disassembler.bytecode;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author maxim
 * Date: 12/29/2023
 */
@EqualsAndHashCode
public class ByteCodeUnits implements Cloneable {
    private final List<ByteCodeUnit> units = new LinkedList<>();

    public ByteCodeUnits() {

    }

    public ByteCodeUnits(@NonNull Collection<ByteCodeUnit> col) {
        units.addAll(col);
    }

    public void clear() {
        units.clear();
    }

    public Collection<ByteCodeUnit> getUnits() {
        return Collections.unmodifiableCollection(units);
    }

    public void addAll(@NonNull Collection<ByteCodeUnit> col) {
        units.addAll(col);
    }

    public void add(@NonNull ByteCodeUnits byteCodeUnits) {
        this.units.addAll(byteCodeUnits.units);
    }

    public int getPatternCount() {
        int count = 0;
        for (ByteCodeUnit unit : units) {
            if (unit.getType() == ByteCodeType.Pattern) {
                count++;
            }
        }
        return count;
    }

    public ByteCodeUnit getPattern(final int i) {
        if (i < 0 || i >= units.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int num = 0;
        for (ByteCodeUnit unit : units) {
            if (unit.getType() == ByteCodeType.Pattern) {
                if (i == num) {
                    return unit;
                }
                num++;
            }
        }
        return null;
    }

    public ByteCodeUnit getPattern(@NonNull final String pattern) {
        int i = 0;
        for (ByteCodeUnit unit : units) {
            if ((unit.getType() == ByteCodeType.Pattern) &&
                    (pattern.equals(unit.getValue()))) {
                return unit;
            }
        }
        return null;
    }

    public int getOffsetInBytes(final int i) {
        if (i < 0 || i >= units.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int size = 0;
        int num = 0;
        for (ByteCodeUnit unit : units) {
            if ((unit.getType() == ByteCodeType.Pattern) && (i == num)) {
                return size;
            }
            size += unit.size();
            num++;
        }
        return size;
    }

    public int count() {
        return units.size();
    }

    public int size() {
        int size = 0;
        for (ByteCodeUnit unit : units) {
            size += unit.size();
        }
        return size;
    }

    public int indexOfPattern(final String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return -1;
        }
        int i = 0;
        for (ByteCodeUnit unit : units) {
            if (unit.getType() == ByteCodeType.Pattern) {
                if (pattern.equals(unit.getValue())) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    @Override
    public ByteCodeUnits clone() {
        final ByteCodeUnits instance = new ByteCodeUnits(getUnits());
        return instance;
    }
}
