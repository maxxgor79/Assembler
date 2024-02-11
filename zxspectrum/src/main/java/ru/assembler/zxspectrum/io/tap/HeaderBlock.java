package ru.assembler.zxspectrum.io.tap;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.LEDataInputStream;
import ru.assembler.io.LEDataOutputStream;

/**
 * @author Maxim Gorin
 */
@Slf4j
@ToString(callSuper = true)
public class HeaderBlock extends Block {

    public static final int TYPE = 0x00;

    public static final int SIZE = 19;

    @Setter
    @Getter
    @NonNull
    private HeaderType headerType;

    @NonNull
    private final byte[] filename = new byte[10];

    @Setter
    @Getter
    private int dataBlockLength;

    @Getter
    @NonNull
    private ArrayParams numberParams;

    @Getter
    @NonNull
    private ArrayParams charParams;

    @Getter
    @NonNull
    private ProgramParams programParams;

    @Setter
    @NonNull
    private BytesParams bytesParams;

    public HeaderBlock() {
        setFlag(Flag.Header);
        blockLength = SIZE;
    }

    public HeaderBlock(ProgramParams programParams) {
        this();
        setProgramParams(programParams);
    }

    public HeaderBlock(BytesParams bytesParams) {
        this();
        setBytesParams(bytesParams);
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        final LEDataInputStream dis = new LEDataInputStream(is);
        final int b = dis.read();
        headerType = HeaderType.getByCode(b);
        if (headerType == null) {
            throw new IOException("Bad header type: " + b);
        }
        final int readBytes = is.read(filename);
        if (readBytes != filename.length) {
            throw new EOFException();
        }
        dataBlockLength = dis.readUnsignedShort();
        if (dataBlockLength > 65535) {
            throw new IOException("Bad block length: " + dataBlockLength);
        }
        switch (headerType) {
            case Program -> {
                programParams = new ProgramParams();
                programParams.read(dis);
            }
            case CharArray -> {
                charParams = new ArrayParams();
                charParams.read(dis);
            }
            case Bytes -> {
                bytesParams = new BytesParams();
                bytesParams.read(dis);
            }
            case NumberArray -> {
                numberParams = new ArrayParams();
                numberParams.read(dis);
            }
        }
        checkSum = dis.read();
        final int calculated = calcCheckSum();
        if (checkSum != calculated) {
            throw new IllegalStateException(
                    "Bad checksum, loaded=" + checkSum + ", calculated=" + calculated);
        } else {
            if (log.isInfoEnabled()) {
                log.info("checkSum is OK");
            }
        }
    }

    @Override
    public void write(@NonNull final OutputStream os) throws IOException {
        final LEDataOutputStream dos = new LEDataOutputStream(os);
        dos.writeShort(blockLength);
        export(os);
    }

    @Override
    public void export(@NonNull OutputStream os) throws IOException {
        os.write(flag.getCode());
        final byte[] data = getBytes();
        os.write(data);
        os.write(checkSum = TapUtils.calculateChecksum(data));
    }


    private byte[] getBytes() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final LEDataOutputStream dos = new LEDataOutputStream(baos);
            if (headerType != null) {
                dos.write(headerType.getCode());
            }
            dos.write(filename);
            dos.writeShort(dataBlockLength);
            if (headerType != null) {
                switch (headerType) {
                    case Program -> {
                        if (programParams != null) {
                            dos.write(programParams.getBytes());
                        }
                    }
                    case CharArray -> {
                        if (charParams != null) {
                            dos.write(charParams.getBytes());
                        }
                    }
                    case Bytes -> {
                        if (bytesParams != null) {
                            dos.write(bytesParams.getBytes());
                        }
                    }
                    case NumberArray -> {
                        if (numberParams != null) {
                            dos.write(numberParams.getBytes());
                        }
                    }
                }
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    public String getFilename() {
        return new String(filename);
    }

    public void setFilename(@NonNull final String filename) {
        final byte[] src = filename.getBytes();
        Arrays.fill(this.filename, (byte) 32);
        System.arraycopy(src, 0, this.filename, 0, src.length > 10 ? 10 : src.length);
        checkSum = calcCheckSum();
    }

    @Override
    protected int calcCheckSum() {
        return TapUtils.calculateChecksum(getBytes());
    }

    @Override
    public int size() {
        return SIZE;
    }

    public void setBytesParams(@NonNull BytesParams bytesParams) {
        if (this.bytesParams == bytesParams) {
            return;
        }
        headerType = HeaderType.Bytes;
        this.bytesParams = bytesParams;
        checkSum = calcCheckSum();
    }

    public void setProgramParams(@NonNull ProgramParams programParams) {
        if (this.programParams == programParams) {
            return;
        }
        headerType = HeaderType.Program;
        this.programParams = programParams;
        checkSum = calcCheckSum();
    }

    public void setCharParams(@NonNull ArrayParams charParams) {
        if (this.charParams == charParams) {
            return;
        }
        this.headerType = HeaderType.CharArray;
        this.charParams = charParams;
        checkSum = calcCheckSum();
    }

    public void setNumberParams(@NonNull ArrayParams numberParams) {
        if (this.numberParams == numberParams) {
            return;
        }
        this.headerType = HeaderType.NumberArray;
        this.numberParams = numberParams;
        checkSum = calcCheckSum();
    }
}
