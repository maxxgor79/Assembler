package ru.assembler.core.io.image;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.io.LEDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@Slf4j
public class BmpReader {
    public static final int BI_RGB = 0;

    public static final int BI_RLE8 = 1;

    public static final int BI_RLE4 = 2;

    public static final int BI_BITFIELDS = 3;

    public static final int BI_JPE = 4;

    public static final int BI_PNG = 5;

    public static final int BI_ALPHABITFIELDS = 6;

    public static final int BI_CMYK = 11;

    public static final int BI_CMYKRLE8 = 12;

    public static final int BI_CMYKRLE4 = 13;

    @Getter
    private Header header;

    @Getter
    private BitmapCoreHeader bitmapCoreHeader;

    @Getter
    private BitmapInfoHeader bitmapInfoHeader;

    @Getter
    private int[] extraBitMasks;

    @Getter
    private byte[] palette;

    @Getter
    protected byte[] content;

    protected void reset() {
        header = null;
        bitmapCoreHeader = null;
        bitmapInfoHeader = null;
        extraBitMasks = null;
        palette = null;
        content = null;
    }

    public BmpReader(@NonNull final InputStream is) throws IOException {
        read(is);
    }

    public void read(@NonNull final InputStream is) throws IOException {
        LEDataInputStream ledis = new LEDataInputStream(is);
        reset();
        readBitmap(ledis);
    }

    public int getWidth() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getWidth();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getWidth();
        }
        return 0;
    }

    public int getHeight() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getHeight();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getHeight();
        }
        return 0;
    }

    public int getBps() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getBps();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getBps();
        }
        return 0;
    }

    public int getCompression() {
        if (bitmapCoreHeader != null) {
            return BI_RGB;
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getCompression();
        }
        return 0;
    }

    protected void readBitmap(@NonNull final LEDataInputStream ledis) throws IOException {
        header = new Header();// read header
        header.read(ledis);
        int size = ledis.readInt();//read dib header size
        switch (size) {
            case 12:
                bitmapCoreHeader = new BitmapCoreHeader(size);
                bitmapCoreHeader.read(ledis);// read dib header
                int paletteSize = 0;
                if (bitmapInfoHeader.getBps() == 4 || bitmapInfoHeader.getBps() == 8) {
                    paletteSize = readPalette(ledis);
                }
                readContent(ledis, header.getFileSize() - header.getOffset() - paletteSize);
                break;
            case 40:
                bitmapInfoHeader = new BitmapInfoHeader(size);
                bitmapInfoHeader.read(ledis);// read dib header
                if (bitmapInfoHeader.getCompression() == BI_BITFIELDS || bitmapInfoHeader.getCompression()
                        == BI_ALPHABITFIELDS) {
                    readExtraBitMasks(ledis);
                }
                if (bitmapInfoHeader.getBps() == 4 || bitmapInfoHeader.getBps() == 8) {
                    readPalette(ledis);
                }
                readContent(ledis, bitmapInfoHeader.getImageSize());
                break;
            default:
                throw new UnsupportedEncodingException();
        }
    }

    private void readExtraBitMasks(@NonNull final LEDataInputStream ledis) throws IOException {
        switch (getCompression()) {
            case BI_BITFIELDS:
                extraBitMasks = new int[3];
                extraBitMasks[0] = ledis.readInt();
                extraBitMasks[1] = ledis.readInt();
                extraBitMasks[2] = ledis.readInt();
                break;
            case BI_ALPHABITFIELDS:
                extraBitMasks = new int[4];
                extraBitMasks[0] = ledis.readInt();
                extraBitMasks[1] = ledis.readInt();
                extraBitMasks[2] = ledis.readInt();
                extraBitMasks[3] = ledis.readInt();
                break;
        }
    }

    protected int readPalette(@NonNull final LEDataInputStream ledis) throws IOException {
        int paletteSize = (1 << bitmapInfoHeader.getBps()) * 4;
        palette = new byte[paletteSize];
        ledis.read(palette);
        return paletteSize;
    }

    protected void readContent(@NonNull final LEDataInputStream ledism, int size) throws IOException {
        content = new byte[size];
        ledism.read(content);
        if (getCompression() == BI_RGB) {
            content = normalize(content);
        }
    }

    //needs to remove paddings
    protected byte[] normalize(byte[] data) throws IOException {
        final int realBytesPerRow = (getBps() < 8) ? getWidth() / (8 / getBps()) : getWidth() * getBps();
        final int bytesPerRow = data.length / getHeight();
        final byte[] buf = new byte[realBytesPerRow];
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < getHeight(); i++) {
            int readBytes = bais.read(buf);
            if (readBytes != buf.length) {
                throw new IOException("Invalid read size: " + readBytes);
            }
            baos.write(buf);
            bais.skip(bytesPerRow - realBytesPerRow);
        }
        return baos.toByteArray();
    }

    public static class Header {
        @Getter
        protected final int size = 14;

        protected static final String ID = "BM";

        @Getter
        private String id;

        @Getter
        protected int fileSize;

        @Getter
        protected int offset;

        @Getter
        protected int reserved1;

        @Getter
        protected int reserved2;

        protected void read(@NonNull final LEDataInputStream ledis) throws IOException {
            final byte[] buffer = new byte[2];
            ledis.read(buffer); //ID
            id = new String(buffer, 0, 2);
            if (!ID.equals(id)) {
                throw new IOException("Invalid id: " + id);
            }
            fileSize = ledis.readInt();
            if (fileSize <= 0) {
                throw new IOException("Bad bmp size");
            }
            reserved1 = ledis.readShort();//reserved
            reserved2 = ledis.readShort();//reserved
            offset = ledis.readInt();
        }
    }

    public static class BitmapCoreHeader {
        protected BitmapCoreHeader(int size) {
            this.size = size;
        }

        @Getter
        private int size;

        @Getter
        private int width;

        @Getter
        private int height;

        @Getter
        private int colorPlanes;

        @Getter
        private int bps;

        protected void read(@NonNull final LEDataInputStream ledis) throws IOException {
            width = ledis.readShort();
            height = ledis.readShort();
            colorPlanes = ledis.readShort();
            bps = ledis.readShort();
        }
    }

    public static class BitmapInfoHeader {
        protected BitmapInfoHeader(int size) {
            this.size = size;
        }

        @Getter
        private int size;

        @Getter
        private int width;

        @Getter
        private int height;

        @Getter
        private int colorPlanes;

        @Getter
        private int bps;

        @Getter
        private int compression;

        @Getter
        private int imageSize;

        @Getter
        private int hResolution;

        @Getter
        private int vResolution;

        @Getter
        private int paletteColors;

        @Getter
        private int importantColors;

        protected void read(@NonNull final LEDataInputStream ledis) throws IOException {
            width = ledis.readInt();
            height = ledis.readInt();
            colorPlanes = ledis.readShort();
            if (colorPlanes != 1) {
                throw new IOException("colorsPlanes != 1");
            }
            bps = ledis.readShort();
            compression = ledis.readInt();
            imageSize = ledis.readInt();
            if (imageSize <= 0) {
                throw new IOException("Bad image size: " + imageSize);
            }
            hResolution = ledis.readInt();
            vResolution = ledis.readInt();
            paletteColors = ledis.readInt();
            importantColors = ledis.readInt();
        }
    }
}
