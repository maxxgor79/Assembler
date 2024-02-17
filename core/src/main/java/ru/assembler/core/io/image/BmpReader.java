package ru.assembler.core.io.image;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.io.LEDataInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
    @NonNull
    protected byte[] content;

    @Getter
    private Header header;

    @Getter
    private BitmapCoreHeader bitmapCoreHeader;

    @Getter
    private BitmapInfoHeader bitmapInfoHeader;

    @Getter
    private byte[] palette;

    public BmpReader(@NonNull final InputStream is) throws IOException {
        read(is);
    }

    public void read(@NonNull final InputStream is) throws IOException {
        LEDataInputStream ledis = new LEDataInputStream(is);
        readBitmap(ledis);
    }

    public int getWidth() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getWidth();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getWidth();
        }
        throw new UnsupportedOperationException();
    }

    public int getHeight() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getHeight();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getHeight();
        }
        throw new UnsupportedOperationException();
    }

    public int getBps() {
        if (bitmapCoreHeader != null) {
            return BI_RGB;
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getBps();
        }
        throw new UnsupportedOperationException();
    }

    public int getCompression() {
        if (bitmapCoreHeader != null) {
            return bitmapCoreHeader.getBps();
        }
        if (bitmapInfoHeader != null) {
            return bitmapInfoHeader.getCompression();
        }
        throw new UnsupportedOperationException();
    }

    protected void readBitmap(@NonNull final LEDataInputStream ledis) throws IOException {
        header = new Header();// read header
        header.read(ledis);
        int size = ledis.readInt();//read dib header size
        switch (size) {
            case 12:
                bitmapCoreHeader = new BitmapCoreHeader(size);
                bitmapCoreHeader.read(ledis);// read dib header
                if (bitmapInfoHeader.getBps() == 4 || bitmapInfoHeader.getBps() == 8) {
                    readPalette(ledis);
                }
                readContent(ledis, header.getFileSize() - header.getOffset());
                break;
            case 40:
                bitmapInfoHeader = new BitmapInfoHeader(size);
                bitmapInfoHeader.read(ledis);// read dib header
                if (bitmapInfoHeader.getBps() == 4 || bitmapInfoHeader.getBps() == 8) {
                    readPalette(ledis);
                }
                readContent(ledis, bitmapInfoHeader.getImageSize());
                break;
            default:
                throw new UnsupportedEncodingException();
        }
    }

    protected void readPalette(@NonNull final LEDataInputStream ledis) throws IOException {
        int paletteSize = (1 << bitmapInfoHeader.getBps()) * 4;
        palette = new byte[paletteSize];
        ledis.read(palette);
    }

    protected void readContent(@NonNull final LEDataInputStream ledism, int size) throws IOException {
        content = new byte[size];
        ledism.read(content);
        content = normalize(content);
    }

    //needs to remove paddings
    protected byte[] normalize(byte[] data) {
        //int bytesPerRow = (getBps() < 8) ? getWidth() / getBps() : getWidth() * getBps();
        return data;
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
