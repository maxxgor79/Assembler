package ru.zxspectrum.basic.compile;

import lombok.NonNull;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.LexemType;
import ru.zxspectrum.basic.Operator;
import ru.zxspectrum.basic.Parser;
import ru.zxspectrum.basic.ParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Compiler {
    private static final int TYPE = 0xff;

    private InputStream is;

    public Compiler() {

    }

    public Compiler(@NonNull byte[] data) {
        setData(data);
    }

    public Compiler(@NonNull InputStream is) {
        setInputStream(is);
    }

    public void setData(@NonNull String s) {
        setData(s.getBytes());
    }

    public void setData(@NonNull byte[] data) {
        setInputStream(new ByteArrayInputStream(data));
    }

    public void setInputStream(@NonNull InputStream is) {
        this.is = is;
    }

    public byte[] compile() throws IOException, ParserException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SourceParser parser = new SourceParser();
        parser.setInputStream(is);
        final List<Lexem> lexemList = parser.parse();
        System.out.println("LIST:"+lexemList);
        baos.write(0);
        baos.write(0);
        baos.write(TYPE);
        translate(baos, lexemList);
        byte[] bytes = baos.toByteArray();
        int bytesSize2 = bytes.length + 2;
        bytes[0] = (byte) bytesSize2;
        bytes[1] = (byte) (bytesSize2 >>> 8);
        return bytes;
    }

    private void translate(OutputStream os, List<Lexem> lexemList) throws IOException {
        List<Lexem> lineLexemList = new LinkedList<>();
        for (Lexem lexem : lexemList) {
            lineLexemList.add(lexem);
            if (lexem.getType() == LexemType.Eol) {
                os.write(translateLine(lineLexemList));
                lineLexemList.clear();
            }
        }
    }

    private byte[] translateLine(List<Lexem> lineLexemList) throws IOException {
        System.out.println(lineLexemList);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        boolean pendingNumber = true;
        for (Lexem lexem : lineLexemList) {
            if (pendingNumber && lexem.getType() == LexemType.Number) {
                int lineNumber = lexem.getIntValue();
                bo.write((byte) (lineNumber >>> 8));
                bo.write((byte) lineNumber);//bigendian
                bo.write(0);
                bo.write(0);//line length in bytes
                pendingNumber = false;
            } else {
                switch (lexem.getType()) {
                    case Number -> {
                        translateNumber(bo, lexem);
                    }
                    case String -> {
                        translateString(bo, lexem);
                    }
                    case Identifier -> {
                        translateIdentifier(bo, lexem);
                    }
                    case Symbol -> {
                        translateSymbol(bo, lexem);
                    }
                    case Delimiter -> {
                        translateDelimiter(bo, lexem);
                    }
                    case Eol -> {
                        translateEol(bo, lexem);
                    }
                }
            }
        }

        byte[] bytes = bo.toByteArray();
        int strLen = bytes.length - 4;
        bytes[2] = (byte) strLen;
        bytes[3] = (byte) (strLen >>> 8);
        return bytes;
    }

    private void translateEol(OutputStream bo, Lexem lexem) throws IOException {
        bo.write(0x0d);
    }

    private void translateDelimiter(OutputStream os, Lexem lexem) throws IOException {
        final Operator o = Operator.get(lexem.getValue());
        if (o == null) {
            os.write(lexem.getValue().getBytes());
        } else {
            os.write(o.getCode());
        }
    }

    private void translateSymbol(OutputStream os, Lexem lexem) throws IOException {
        os.write(lexem.getIntValue());
    }

    private void translateIdentifier(OutputStream os, Lexem lexem) throws IOException {
        final Operator o = Operator.get(lexem.getValue());
        if (o == null) {
            os.write(lexem.toString().getBytes());
        } else {
            os.write(o.getCode());
        }
    }

    private void translateString(OutputStream os, Lexem lexem) throws IOException {
        String s = lexem.toString();
        for (int i = 0; i < s.length(); i++) {
            os.write(s.charAt(i));
        }
    }

    private void translateNumber(OutputStream os, Lexem lexem) throws IOException {
        os.write(lexem.getValue().getBytes());
        os.write(0x0e);
        os.write(Parser.intToBytes5(lexem.getIntValue()));
    }
}
