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
public class ByteCodeUnits {
    private final List<ByteCodeUnit> units = new LinkedList<>();

    public ByteCodeUnits() {

    }

    public ByteCodeUnits(@NonNull Collection<ByteCodeUnit> col) {
        units.addAll(col);
    }

    public void clear() {
        units.clear();
    }

    public Collection<ByteCodeUnit> toCollection() {
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

    public int getByteCodeSize(final int i) {
        int size = 0;
        int num = 0;
        for (ByteCodeUnit unit : units) {
            if ((unit.getType() == ByteCodeType.Pattern) && (i == num)) {
                return size;
            }
            size += unit.getByteCodeSize();
            num++;
        }
        return size;
    }

    public int size() {
        return units.size();
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
}
