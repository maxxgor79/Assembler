import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * @Author: Maxim Gorin
 * Date: 02.03.2024
 */
public class BuildVersion {
    private static final Logger log = Logger.getLogger(BuildVersion.class.getName());

    public static final String FILE_NAME = "build.version";

    public static void main(String... args) {
        final String path = args[0];
        if (path == null) {
            log.info("path is null");
            return;
        }
        final File dir = new File(path);
        if (!dir.exists()) {
            log.info("Directory does not exist: " + dir.getAbsolutePath());
            return;
        }
        if (dir.isFile()) {
            log.info("Directory required: " + dir.getAbsolutePath());
            return;
        }
        final File file = new File(dir, FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.throwing(BuildVersion.class.getName(), "main", e);
                return;
            }
        }
        try {
            incrementVersion(file);
        } catch (IOException e) {
            log.throwing(BuildVersion.class.getName(), "main", e);
        }
    }

    private static void incrementVersion(File file) throws IOException {
        long version = 1;
        long buildDate = System.currentTimeMillis();
        try {
            final String s = Files.readString(file.toPath());
            final String [] parsed = s.split(" ");
            version = Long.parseLong(parsed[0]);
            version++;
            buildDate = file.lastModified();
        } catch (Exception e) {
            log.throwing(BuildVersion.class.getName(), "incrementVersion", e);
        }
        final String data = String.format("%04d %d", version, buildDate);
        System.out.println("Version: " + version);
        Files.writeString(file.toPath(), data, Charset.defaultCharset(), StandardOpenOption
                .WRITE);
    }
}
