package ru.zxspectrum.assembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.ns.AbstractNamespaceApi;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.resource.Loader;
import ru.zxspectrum.assembler.settings.AssemblerSettings;
import ru.zxspectrum.assembler.settings.ResourceSettings;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
import ru.zxspectrum.assembler.util.FileUtil;
import ru.zxspectrum.assembler.util.SymbolUtils;
import ru.zxspectrum.assembler.util.TypeUtil;
import ru.zxspectrum.io.tap.TapData;
import ru.zxspectrum.io.tap.TapUtil;
import ru.zxspectrum.io.wav.SoundGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Maxim Gorin
 */

@Slf4j
public class Assembler extends AbstractNamespaceApi {

    public static final String EXT_TAP = "tap";

    public static final String EXT_WAV = "wav";

    private final Map<BigInteger, PostCommandCompiler> postCommandCompilerMap = new LinkedHashMap<>();

    private final AssemblerSettings settings = new AssemblerSettings();

    private BigInteger address = new BigInteger("8000", 16);

    public Assembler() {
        reset();
        loadSettings();
    }

    @Override
    public void reset() {
        super.reset();
        postCommandCompilerMap.clear();
    }

    private void loadSettings() {
        try {
            ResourceSettings resourceSettings = new ResourceSettings();
            resourceSettings.load("settings.properties");
            settings.merge(resourceSettings);
            setDefaultSettings();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void run(@NonNull final File... files) throws IOException {
        OutputStream os = null;
        try {
            for (File file : files) {
                File outputFile = createOutputFile(file);
                try {
                    reset();
                    os = new FileOutputStream(outputFile);
                    final CompilerApi compilerApi = runSingle(file, os);
                    postCompile(outputFile);
                    Output.formatPrintln("%d %s", Output.getWarningCount(),
                            MessageList.getMessage(MessageList.N_WARNINGS));
                    Output.formatPrintln("%s %s %d %s, %d %s", MessageList.getMessage(MessageList.COMPILED1),
                            MessageList.getMessage(MessageList.SUCCESSFULLY), compilerApi.getCompiledLineCount(),
                            MessageList
                                    .getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount(), MessageList
                                    .getMessage(MessageList.SOURCES));
                } finally {
                    FileUtil.safeClose(os);
                }
                if (settings.isProduceTap()) {
                    createTap(outputFile, getAddress());
                }
                if (settings.isProduceWav()) {
                    createWav(outputFile, getAddress());
                }
            }
        } catch (Exception e) {
            Output.println(e.getMessage());
            log.error(e.getMessage(), e);
        }
    }

    private void createWav(final File file, @NonNull final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(file);
        final TapData tapData = TapUtil.createBinaryTap(data, address.intValue());
        final File wavFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, EXT_WAV);
        final SoundGenerator sg = new SoundGenerator(wavFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tapData);
    }

    private TapData createTap(final File file, @NonNull final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(file);
        final File tapFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, EXT_TAP);
        return TapUtil.createBinaryTap(tapFile, data, address.intValue());
    }

    private File createOutputFile(final File file) {
        if (!settings.getOutputDirectory().exists()) {
            settings.getOutputDirectory().mkdirs();
        }
        return FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, null);
    }

    protected CompilerApi runSingle(final File file, final OutputStream os) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            CompilerApi compilerApi = CompilerFactory.create(this, settings, file, fis, os);
            compilerApi.compile();
            return compilerApi;
        }
    }

    private String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(MessageList.getMessage(MessageList.PROGRAM_WELCOME),
                settings.getMajorVersion()
                , settings.getMinorVersion());
        String writtenBy = MessageList.getMessage(MessageList.WRITTEN_BY);
        String lineExternal = SymbolUtils.fillChar('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(SymbolUtils.fillChar(' ', 78))
                .append('*')).toString();
        sb.append(
                SymbolUtils.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
                        , programWelcome)).append(System.lineSeparator());
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2
                , writtenBy)).append(System.lineSeparator());
        sb.append(lineExternal).append(System.lineSeparator());
        return sb.toString();
    }

    private List<File> setCli(String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            final CommandLine cli = parser.parse(options, args);
            settings.load(cli);
            setDefaultSettings();
            final List<File> files = new LinkedList<>();
            for (final String fileName : cli.getArgList()) {
                files.add(new File(fileName));
            }
            return files;
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return Collections.emptyList();
    }

    private void setDefaultSettings() {
        if (settings.getAddress() != null) {
            setAddress(settings.getAddress());
        }
    }

    //----------------------------------------------------------
    @Override
    public void setAddress(@NonNull final BigInteger address) {
        if (address.signum() == -1) {
            throw new IllegalArgumentException("address is negative");
        }
        if (!TypeUtil.isInRange(settings.getMinAddress(), settings.getMaxAddress(), address)) {
            throw new IllegalArgumentException("address is out of range");
        }
        this.address = address;
    }

    @Override
    public BigInteger getAddress() {
        return this.address;
    }

    @Override
    public void addToQueue(@NonNull PostCommandCompiler postCommandCompiler) {
        postCommandCompilerMap.put(postCommandCompiler.getCommandOffset(), postCommandCompiler);
    }

    protected void postCompile(final File outputFile) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(outputFile, "rwd");
            for (Map.Entry<BigInteger, PostCommandCompiler> entry : postCommandCompilerMap.entrySet()) {
                entry.getValue().compile(randomAccessFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            FileUtil.safeClose(randomAccessFile);
        }
    }

    public static void main(final String[] args) throws Exception {
        Options options = getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("z80asm <file1>...<fileN>", options);
            return;
        }
        Assembler assembler = new Assembler();
        List<File> fileList = assembler.setCli(args, options);
        if (!fileList.isEmpty()) {
            Output.println(assembler.createWelcome());
            assembler.run(fileList.toArray(new File[fileList.size()]));
        } else {
            Output.println("No input files");
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("st", "strict-type-conversion", false
                , "Turn on strict type conversation");
        options.addOption("a", "address", true, "'org' address." +
                " Non negative value.");
        options.addOption("min", "min-address", true, "minimal address." +
                " Non negative value.");
        options.addOption("max", "max-address", true, "maximal address." +
                " Non negative value.");
        options.addOption("o", "output", true, "output directory for" +
                " compiled files.");
        options.addOption("b", "byte-order", true, "byte order" +
                ": little-endian or big-endian.");
        options.addOption("s", "source-encoding", true, "source encoding. UTF-8 is default.");
        options.addOption("p", "platform-encoding", true, "platform encoding. ASCII is default.");
        options.addOption("tap", false, "to produce in <TAP> format.");
        options.addOption("wav", false, "to produce in <WAV> format.");
        return options;
    }
}