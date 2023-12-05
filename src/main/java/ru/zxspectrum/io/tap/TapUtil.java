package ru.zxspectrum.io.tap;

import lombok.NonNull;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class TapUtil {
    private TapUtil() {

    }

    private static Block createHeader() {
        /*
        final BytesParams bytesParams = new BytesParams();
        bytesParams.setStartAddress(address);

        final Header header = new Header();
        header.setHeaderType(HeaderType.Bytes);
        header.setFilename("data");
        header.setDataSize(data.length);
        header.setBytesParams(bytesParams);

        final Block block = new Block();
        block.setData(data);
        block.setDataSize(19);
        block.setFlag(Flag.Header);
        block.setHeader(header);

         */
        return null;
    }

    private static Block createData() {
        /*
        final BytesParams bytesParams = new BytesParams();
        bytesParams.setStartAddress(address);

        final Header header = new Header();
        header.setHeaderType(HeaderType.Bytes);
        header.setFilename("data");
        header.setDataSize(data.length);
        header.setBytesParams(bytesParams);

        final Block block = new Block();
        block.setData(data);
        block.setDataSize(19);
        block.setFlag(Flag.Header);
        block.setHeader(header);

         */
        return null;
    }

    public static void createBinaryTap(@NonNull File file, @NonNull byte[] data, int address) throws IOException {

        final TapData tapData = new TapData();
        tapData.add(createHeader());
        tapData.add(createData());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            tapData.write(new LEDataOutputStream(fos));
        }
    }
}
