package ru.zxspectrum.disassembler.error;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author maxim
 * Date: 1/4/2024
 */
@EqualsAndHashCode
@ToString
public class ExceptionGroup extends RuntimeException {
    private final Collection<Throwable> exceptions;

    public ExceptionGroup(@NonNull Collection<Throwable> c) {
        exceptions = new LinkedList<>();
        exceptions.addAll(c);
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    public Collection<Throwable> getExceptions() {
        return Collections.unmodifiableCollection(exceptions);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (Throwable t : exceptions) {
            sb.append(t.getMessage()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public String getLocalizedMessage() {
        StringBuilder sb = new StringBuilder();
        for (Throwable t : exceptions) {
            sb.append(t.getLocalizedMessage()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public Throwable getCause() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printStackTrace() {
        for (Throwable t : exceptions) {
            t.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        for (Throwable t : exceptions) {
            t.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        for (Throwable t : exceptions) {
            t.printStackTrace(s);
        }
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        List<StackTraceElement> elementList = new LinkedList<>();
        for (Throwable t : exceptions) {
            elementList.addAll(Arrays.asList(t.getStackTrace()));
        }
        return elementList.toArray(new StackTraceElement[elementList.size()]);
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        throw new UnsupportedOperationException();
    }
}
