package ru.retro.assembler.i8080.editor.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Author: Maxim Gorin Date: 20.02.2024
 */
@Slf4j
public final class ResourceUtils {
    private ResourceUtils() {

    }

    public static Image loadImage(@NonNull String path) throws IOException {
        byte[] iconData = IOUtils.toByteArray(ResourceUtils.class.getResourceAsStream(path));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(iconData));
        return image;
    }

    public static Icon loadIcon(@NonNull String path) throws IOException {
        Image image = loadImage(path);
        return new ImageIcon(image);
    }

    public static String cutExtension(@NonNull final String path) {
        int index = FilenameUtils.indexOfExtension(path);
        if (index == -1) {
            return path;
        }
        return path.substring(0, index);
    }

    static int scanVertic(boolean[][] matrix, int x, int y) {
        int len = 1;
        for (int i = y + 1; i < 8; i++) {
            if (matrix[x][i] == true) {
                len++;
            } else {
                break;
            }
        }
        return len;
    }

    static int resetVertic(boolean[][] matrix, int x, int y) {
        int len = 1;
        for (int i = y; i < 8; i++) {
            if (matrix[x][i] == true) {
                matrix[x][i] = false;
                len++;
            } else {
                break;
            }
        }
        return len;
    }

    static int scanHoriz(boolean[][] matrix, int x, int y) {
        int len = 1;
        for (int i = x + 1; i < 8; i++) {
            if (matrix[i][y] == true) {
                len++;
            } else {
                break;
            }
        }
        return len;
    }

    static int resetHoriz(boolean[][] matrix, int x, int y) {
        int len = 1;
        for (int i = x; i < 8; i++) {
            if (matrix[i][y] == true) {
                matrix[i][y] = false;
                len++;
            } else {
                break;
            }
        }
        return len;
    }

    public static byte[] loadResource(@NonNull String path) throws IOException {
        final InputStream is = ResourceUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        return IOUtils.toByteArray(is);
    }

    static String getRectangle(boolean [][] matrix, int i, int j, double scale) {
        int vertLength = scanVertic(matrix, i, j);
        int horizLength = scanHoriz(matrix, i, j);
        if (vertLength > horizLength) {
            resetVertic(matrix, i, j);
            return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\"/>"
                    , (int) (i * scale), (int) (j * scale), (int) scale, (int) (vertLength * scale));
        } else {
            resetHoriz(matrix, i, j);
            return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\"/>"
                    , (int) (i * scale), (int) (j * scale), (int) (scale * horizLength), (int) scale);
        }
    }

    static String toSvg(boolean matrix[][], double scale) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<svg viewBox=\"0 0 %d %d\" xmlns=\"http://www.w3.org/2000/svg\">"
                , (int) (8 * scale), (int) (8 * scale)));
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (matrix[i][j] == true) {
                    String rect = getRectangle(matrix, i, j, scale);
                    sb.append(rect);
                }
            }
        sb.append("</svg>");
        return sb.toString();
    }

    public static void toPng(String path) {
        byte[] font;
        int counter = 0;
        try (FileInputStream fis = new FileInputStream(path)) {
            font = IOUtils.toByteArray(fis);
            BufferedImage image = new BufferedImage(2048, 8, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();  // not sure on this line, but this seems more right
            g.setColor(Color.black);
            g.fillRect(0, 0, 2048, 8); // give the whole image a white background
            g.setColor(Color.white);
            for (int x = 0; x < 256; x++) {
                final BufferedImage image2 = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
                final Graphics2D g2 = image2.createGraphics();  // not sure on this line, but this seems more right
                g2.setColor(Color.black);
                g2.fillRect(0, 0, 8, 8); // give the whole image a white background
                g2.setColor(Color.white);
                boolean matrix[][] = new boolean[8][8];
                for (int y = 0; y < 8; y++) {
                    int b = (font[x * 8 + y] & 0xff);
                    int mask = 0b1000_0000;
                    for (int i = 0; i < 8; i++, b <<= 1) {
                        boolean on = (b & mask) == mask;
                        if (on) {
                            g.drawLine(x * 8 + i, y, x * 8 + i, y);
                            g2.drawLine(i, y, i, y);
                            matrix[i][y] = false;
                        } else {
                            matrix[i][y] = true;
                        }
                    }
                }
                ImageIO.write(image2, "png", new File(String.format("%d.png", counter)));
                String svg = toSvg(matrix, 5.0);
                String fileName = String.format("%d.svg", counter);
                IOUtils.write(svg.getBytes(), new FileOutputStream(fileName));
                counter++;
            }
            ImageIO.write(image, "png", new File("output.png"));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}