package ru.zxspectrum.basic.decompile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.basic.Order;
import ru.zxspectrum.basic.ParserException;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class LinesStream {
    private InputStream is;

    public LinesStream() {

    }

    public LinesStream(@NonNull InputStream is) {
        setInputStream(is);
    }

    public LinesStream(@NonNull byte[] data) {
        setData(data);
    }

    public void setData(@NonNull byte[] data) {
        setInputStream(new ByteArrayInputStream(data));
    }

    public void setInputStream(@NonNull InputStream in) {
        this.is = in;
    }

    public Line next() throws ParserException, IOException {
        Line line = new Line();
        line.setLineNumber(nextWord(Order.BigEndian));
        int strLen = nextWord(Order.LittleEndian);
        if (strLen < 0) {
            throw new ParserException("strLen is negative");
        }
        if (strLen > 49152) {
            throw new ParserException("strLen is too long");
        }
        byte[] data = new byte[strLen];
        is.read(data);
        line.setLineData(data);
        return line;
    }

    public Collection<Line> parse() throws ParserException, IOException {
        List<Line> lineList = new LinkedList<>();
        try {
            while (true) {
                lineList.add(next());
            }
        } catch (EOFException e) {
        }
        return lineList;
    }

    private int nextWord(@NonNull Order order) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        if (order == Order.BigEndian) {
            return (ch1 << 8) + (ch2 << 0);
        } else {
            return (ch2 << 8) + (ch1 << 0);
        }
    }

    private void skip(int n) throws IOException {
        for (int i = 0; i < n; i++) {
            int b = is.read();
            if (b == -1) {
                break;
            }
        }
    }
}
