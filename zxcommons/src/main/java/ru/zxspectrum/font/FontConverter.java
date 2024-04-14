package ru.zxspectrum.font;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Maxim Gorin
 * Date: 14.04.2024
 */
@Slf4j
public class FontConverter {
    private static int WIDTH = 8;

    private static int HEIGHT = 8;

    private static int SYMBOLS = 96;

    private byte[] data = new byte[8 * SYMBOLS];

    private boolean[][] symbolData = new boolean[WIDTH][HEIGHT];

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Argument required");
            return;
        }
        FontConverter converter = new FontConverter();
        try (FileInputStream fis = new FileInputStream(args[0])) {
            converter.load(fis);
        }
        converter.convert();
    }

    public void load(@NonNull InputStream is) throws IOException {
        IOUtils.readFully(is, data);
    }

    public void convert() {
        for (int k = 0; k < SYMBOLS; k++) {
            for (int i = 0; i < HEIGHT; i++) {
                int val = data[k * WIDTH + i];
                for (int j = 0; j < WIDTH; j++) {
                    symbolData[j][i] = (val & 0x80) == 0x80 ? true : false;
                    val <<= 1;
                }
            }
            output(symbolData);
            generate(symbolData);
        }
    }

    private void output(boolean[][] symbolData) {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(symbolData[j][i] ? '*' : ' ');
            }
            System.out.println();
        }
    }

    private void generate(boolean[][] symbolData) {

    }
}
