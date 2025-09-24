package ru.zxspectrum.io.trd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Scl2Trd {
    private byte [] emptyArray = new byte[1792];
    private InputStream iStream;
    private OutputStream oStream;
    private byte[] buff = new byte[256];
    private int freeTrack = 1;
    private int freeSec = 0;
    private int count;
    private boolean isFull = true;
    private int totalFreeSect = 2544;

    private void cleanBuffer() {
        Arrays.fill(buff, (byte)0);
    }

    private void validateScl() throws IOException {
        cleanBuffer();
        iStream.read(buff, 0, 8);
        if (!(new String(buff, 0, 8)).equals("SINCLAIR")) {
            System.err.println("Wrong file! Select only SCL files");
            throw new IOException("Wrong file! Select only SCL files");
        }
        writeCatalog();
    }

    private void writeCatalog() throws IOException {
        int i;
        totalFreeSect = 2544;
        freeTrack = 1;
        freeSec = 0;
        count = 0;
        count = iStream.read();
        if (count < 0) {
            System.err.println("Bad count size!");
            throw new IOException();
        }
        for (i = 0; i < count; i++) {
            iStream.read(buff, 0, 14);
            buff[14] = (byte) freeSec;
            buff[15] = (byte) freeTrack;
            freeSec += buff[0xd] & 0xFF;
            freeTrack += freeSec / 16;
            totalFreeSect -= buff[0xd] & 0xFF;
            freeSec = freeSec % 16;
            oStream.write(buff, 0, 16);
        }
        cleanBuffer();
        for (i = count; i < 128;i++) {
            oStream.write(buff, 0, 16);
        }
        System.out.println(" * Disk catalog written");
        writeDiskInfo();
    }

    private void writeDiskInfo() throws IOException {
        cleanBuffer();
        buff[0xe3] = 0x16; // IMPORTANT! 80 track double sided
        buff[0xe4] = (byte) count;
        buff[0xe1] = (byte) freeSec;
        buff[0xe2] = (byte) freeTrack;

        if (isFull) {
            buff[0xe6] = (byte) (totalFreeSect / 256);
            buff[0xe5] = (byte) (totalFreeSect & 255);
        }

        buff[0xe7] = 0x10;
        buff[0xf5] = 's';
        buff[0xf6] = 'c';
        buff[0xf7] = 'l';
        buff[0xf8] = '2';
        buff[0xf9] = 't';
        buff[0xfa] = 'r';
        buff[0xfb] = 'd';
        oStream.write(buff, 0, 256);
        oStream.write(emptyArray); // Any dirt is ok
        System.out.println(" * Disk information written");
        writeDiskData();
    }

    private void writeDiskData() throws IOException {
        int r = iStream.read(buff, 0, 256);
        if (r == -1) {
            System.err.println("Stream is empty!");
            throw new IOException("Stream is empty!");
        }
        while (r == 256) {
            oStream.write(buff, 0, r);
            r = iStream.read(buff, 0, 256);
        }

        if (isFull) {
            cleanBuffer();
            for (r = 0; r < totalFreeSect; r++) {
                oStream.write(buff, 0, 256);
            }
        }
        System.out.println(" * All data written!");
        System.out.println("");
        System.out.println(" TRD file is stored and ready to use!");
        System.out.println("");
        System.out.println("Success!");
    }
    private void run(String [] args) throws IOException {
        try {
            iStream = new FileInputStream(args[0]);
            oStream = new FileOutputStream(args[1]);
            validateScl();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        finally {
            if (iStream != null) {
                try {
                    iStream.close();
                    iStream = null;
                } catch (Exception e1) {
                    e1.fillInStackTrace();
                }
            }
            if (oStream != null) {
                try {
                    oStream.close();
                    oStream = null;
                } catch (Exception e2) {
                    e2.fillInStackTrace();
                }
            }
        }
    }

    public static void main(String []args) throws IOException {
        if (args.length == 0) {
            System.err.println("No input/output files!");
            return;
        }
        if (args.length == 1) {
            System.err.println("No output file!");
            return;
        }
        File input = new File(args[0]);
        if (!input.exists()) {
            System.err.println("Input file does not exist!");
            return;
        }
        File output = new File(args[1]);
        if (output.exists()) {
            System.err.println("Output file already exists!");
            return;
        }
        new Scl2Trd().run(args);
    }
}
