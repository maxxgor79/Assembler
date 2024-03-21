package ru.retro.assembler.editor.core.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * @Author: Maxim Gorin Date: 20.02.2024
 */
@Slf4j
public final class ResourceUtils {
    private static final String WIN_PATH = "rundll32";

    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

    private static final java.util.List<String> browserList = Arrays.asList("google-chrome", "chrome-stable"
            , "firefox", "mozilla", "opera", "epiphany", "konqueror", "chromium", "links"
            , "lynx", "brave-browser");

    private static Icon questionIcon;

    private static Icon errorIcon;

    private static Icon informationIcon;

    private ResourceUtils() {

    }

    public static Image loadImage(@NonNull String path) throws IOException {
        final InputStream is = ResourceUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        final byte[] iconData = IOUtils.toByteArray(is);
        final BufferedImage image = ImageIO.read(new ByteArrayInputStream(iconData));
        return image;
    }

    public static Icon loadIcon(@NonNull String path) throws IOException {
        Image image = loadImage(path);
        return new ImageIcon(image);
    }

    public static Icon getQuestionIcon() {
        if (questionIcon == null) {
            try {
                questionIcon = loadIcon("/icon48x48/question.png");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return questionIcon;
    }

    public static Icon getErrorIcon() {
        if (errorIcon == null) {
            try {
                errorIcon = loadIcon("/icon48x48/error.png");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return errorIcon;
    }

    public static Icon getInformationIcon() {
        if (informationIcon == null) {
            try {
                informationIcon = loadIcon("/icon48x48/information.png");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return informationIcon;
    }

    public static void browse(@NonNull final URI uri) {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(uri);
                    return;
                } catch (UnsupportedOperationException | IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            final String cmd = String.format("%s %s %s", WIN_PATH, WIN_FLAG, uri.getPath());
            try {
                final Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            try {
                Runtime.getRuntime().exec("open " + uri.getPath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            try {
                String browser = null;
                for (int count = 0; count < browserList.size() && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browserList.get(count)}).waitFor() == 0) {
                        browser = browserList.get(count);
                        break;
                    }
                }
                if (browser != null) {
                    Runtime.getRuntime().exec(new String[]{browser, uri.getPath()});
                } else {
                    log.info("No browser found");
                }
            } catch (InterruptedException | IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
