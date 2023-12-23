package ru.zxspectrum.assembler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.ns.AbstractNamespaceApi;
import ru.zxspectrum.assembler.settings.AssemblerSettings;
import ru.zxspectrum.assembler.settings.ResourceSettings;
import ru.zxspectrum.assembler.util.FileUtil;
import ru.zxspectrum.assembler.util.SymbolUtil;
import ru.zxspectrum.assembler.util.TypeUtil;
import ru.zxspectrum.io.tap.TapData;
import ru.zxspectrum.io.tap.TapUtil;
import ru.zxspectrum.io.wav.SoundGenerator;
import ru.zxspectrum.io.wav.WavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author Maxim Gorin
 */

@Slf4j
public class Assembler extends AbstractNamespaceApi {
    protected AssemblerSettings settings;

    protected final Map<BigInteger, PostCommandCompiler> postCommandCompilerMap = new LinkedHashMap<>();

    @Getter
    protected BigInteger address = new BigInteger("8000", 16);

    public Assembler() {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        postCommandCompilerMap.clear();
    }

    protected void setSettings(@NonNull AssemblerSettings settings) {
        this.settings = settings;
        if (settings.getAddress() != null) {
            setAddress(settings.getAddress());
        }
    }

    public void run(@NonNull final File... files) throws IOException {
        OutputStream os = null;
        try {
            for (File file : files) {
                final File outputFile = createOutputFile(file);
                try {
                    reset();
                    os = new FileOutputStream(outputFile);
                    final CompilerApi compilerApi = runSingle(file, os);
                    postCompile(outputFile);
                    Output.formatPrintln("%d %s", Output.getWarningCount(), MessageList.getMessage(MessageList.N_WARNINGS));
                    Output.formatPrintln("%s %s %d %s, %d %s", MessageList.getMessage(MessageList.COMPILED1), MessageList.getMessage(MessageList.SUCCESSFULLY), compilerApi.getCompiledLineCount(), MessageList.getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount(), MessageList.getMessage(MessageList.SOURCES));
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

    private void createWav(final File file, @NonNull final BigInteger address) throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(file);
        final TapData tapData = TapUtil.createBinaryTap(data, address.intValue());
        final File wavFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, WavFile.EXTENSION);
        final SoundGenerator sg = new SoundGenerator(wavFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tapData);
    }

    private TapData createTap(final File file, @NonNull final BigInteger address) throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(file);
        final File tapFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, TapUtil.EXTENSION);
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
        String programWelcome = String.format(MessageList.getMessage(MessageList.PROGRAM_WELCOME), settings.getMajorVersion(), settings.getMinorVersion());
        String writtenBy = MessageList.getMessage(MessageList.WRITTEN_BY);
        String lineExternal = SymbolUtil.fillChar('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(SymbolUtil.fillChar(' ', 78)).append('*')).toString();
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2, programWelcome)).append(System.lineSeparator());
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2, writtenBy)).append(System.lineSeparator());
        sb.append(lineExternal).append(System.lineSeparator());
        return sb.toString();
    }

    private List<File> setCli(String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            final CommandLine cli = parser.parse(options, args);
            settings.load(cli);
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
    public void addToQueue(@NonNull PostCommandCompiler postCommandCompiler) {
        postCommandCompilerMap.put(postCommandCompiler.getCommandOffset(), postCommandCompiler);
    }

    protected void postCompile(@NonNull final File outputFile) {
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
        AssemblerSettings settings = loadSettings();
        Options options = getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(settings.getCmdFilename() + " <file1>...<fileN>", options);
            return;
        }
        final Assembler assembler = new Assembler();
        assembler.setSettings(settings);
        final List<File> fileList = assembler.setCli(args, options);
        if (!fileList.isEmpty()) {
            Output.println(assembler.createWelcome());
            assembler.run(fileList.toArray(new File[fileList.size()]));
        } else {
            Output.println("No input files");
        }
    }

    protected static AssemblerSettings loadSettings() {
        AssemblerSettings settings = new AssemblerSettings();
        try {
            ResourceSettings resourceSettings = new ResourceSettings();
            resourceSettings.load("settings.properties");
            settings.merge(resourceSettings);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return settings;
    }

    protected static Options getOptions() {
        Options options = new Options();
        options.addOption("st", "strict-type-conversion", false, "Turn on strict type conversion");
        options.addOption("a", "address", true, "'org' address." + " Non negative value.");
        options.addOption("min", "min-address", true, "minimal address." + " Non negative value.");
        options.addOption("max", "max-address", true, "maximal address." + " Non negative value.");
        options.addOption("o", "output", true, "output directory for" + " compiled files.");
        options.addOption("b", "byte-order", true, "byte order" + ": little-endian or big-endian.");
        options.addOption("s", "source-encoding", true, "source encoding. UTF-8 is default.");
        options.addOption("p", "platform-encoding", true, "platform encoding. ASCII is default.");
        options.addOption("tap", false, "to produce in <TAP> format.");
        options.addOption("wav", false, "to produce in <WAV> format.");
        return options;
    }
}