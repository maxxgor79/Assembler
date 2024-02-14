package ru.zxspectrum.basic.decompile;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.LineNumber;
import ru.zxspectrum.basic.ParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@ToString
public class Decompiler {
    @Getter
    private int size;

    @Getter
    private int type;

    private InputStream is;

    public Decompiler() {

    }

    public Decompiler(@NonNull byte[] data) {
        setData(data);
    }

    public Decompiler(@NonNull InputStream is) {
        setInputStream(is);
    }

    public void setData(@NonNull byte[] data) {
        setInputStream(new ByteArrayInputStream(data));
    }

    public void setInputStream(@NonNull InputStream is) {
        this.is = is;
    }

    public Collection<Lexem> decompile() throws IOException, ParserException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream is not initialized");
        }
        final List<Lexem> lexemList = new LinkedList<>();
        final LinesStream stream = new LinesStream(is);
        final Collection<Line> lines = stream.parse();
        final LineParser parser = new LineParser();
        for (Line line : lines) {
            lexemList.add(new LineNumber(line.getLineNumber()));
            parser.setData(line.getLineData());
            lexemList.addAll(parser.parse());
        }
        return lexemList;
    }
}
