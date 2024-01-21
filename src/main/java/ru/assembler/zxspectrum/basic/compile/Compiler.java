package ru.assembler.zxspectrum.basic.compile;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.zxspectrum.basic.Lexem;
import ru.assembler.zxspectrum.basic.LexemType;
import ru.assembler.zxspectrum.basic.Operator;
import ru.assembler.zxspectrum.basic.Parser;
import ru.assembler.zxspectrum.basic.ParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public class Compiler {

    private static final int TYPE = 0xff;

    private static final int EOL = 0x0d;

    private static final int NUM5 = 0x0e;


    private InputStream is;

    @Setter
    @Getter
    @NonNull
    private Replacer replacer;

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
        translate(baos, lexemList);
        byte[] bytes = baos.toByteArray();
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
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        boolean pendingNumber = true;
        for (Lexem lexem : lineLexemList) {
            if (lexem.getType() == LexemType.Variable) {
                Lexem value = replacer.getValue(lexem.getValue());
                if (value != null) {
                    lexem = value;
                }
            }
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
        bo.write(EOL);
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
        String identifier = lexem.getValue();
        final Operator o = Operator.get(identifier);
        if (o == null) {
            os.write(identifier.getBytes());
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
        os.write(NUM5);
        os.write(Parser.intToBytes5(lexem.getIntValue()));
    }
}
