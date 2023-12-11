package ru.zxspectrum.io.tap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

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
        flag = Flag.Header;
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
        is.read(filename);
        dataBlockLength = dis.readUnsignedShort();
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
        int calculated = calcCheckSum();
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
        LEDataOutputStream dos = new LEDataOutputStream(os);
        dos.writeShort(blockLength);
        export(os);
    }

    @Override
    public void export(@NonNull OutputStream os) throws IOException {
        os.write(flag.getCode());
        byte[] data = getBytes();
        os.write(data);
        checkSum = TapUtil.calculateChecksum(data);
        os.write(checkSum);
    }


    private byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            LEDataOutputStream dos = new LEDataOutputStream(baos);
            if (headerType != null) {
                dos.writeByte(headerType.getCode());
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
        byte[] src = filename.getBytes();
        Arrays.fill(this.filename, (byte) 32);
        System.arraycopy(src, 0, this.filename, 0, src.length > 10 ? 10 : src.length);
        checkSum = calcCheckSum();
    }

    @Override
    protected int calcCheckSum() {
        return TapUtil.calculateChecksum(getBytes());
    }

    public void setBytesParams(@NonNull BytesParams bytesParams) {
        headerType = HeaderType.Bytes;
        this.bytesParams = bytesParams;
        checkSum = calcCheckSum();
    }

    public void setProgramParams(@NonNull ProgramParams programParams) {
        headerType = HeaderType.Program;
        this.programParams = programParams;
        checkSum = calcCheckSum();
    }

    public void setCharParams(@NonNull ArrayParams charParams) {
        this.headerType = HeaderType.CharArray;
        this.charParams = charParams;
        checkSum = calcCheckSum();
    }

    public void setNumberParams(@NonNull ArrayParams numberParams) {
        this.headerType = HeaderType.NumberArray;
        this.numberParams = numberParams;
        checkSum = calcCheckSum();
    }
}
