package ru.assembler.zxspectrum;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.compiler.PostCommandCompiler;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.error.text.Output;
import ru.assembler.core.ns.AbstractNamespaceApi;
import ru.assembler.core.settings.DefaultSettings;
import ru.assembler.core.settings.ResourceSettings;
import ru.assembler.zxspectrum.core.compiler.Z80Compiler;
import ru.assembler.zxspectrum.core.compiler.option.OptionTypes;
import ru.assembler.zxspectrum.core.settings.Z80AssemblerSettings;
import ru.assembler.zxspectrum.io.tzx.TzxUtils;
import ru.assembler.zxspectrum.io.tzx.TzxWriter;
import ru.assembler.zxspectrum.text.Z80Messages;
import ru.assembler.core.util.FileUtil;
import ru.assembler.core.util.SymbolUtil;
import ru.assembler.core.util.TypeUtil;
import ru.assembler.zxspectrum.io.tap.TapData;
import ru.assembler.zxspectrum.io.tap.TapUtil;
import ru.assembler.zxspectrum.io.generator.SignalGenerator;
import ru.assembler.io.wav.WavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class Z80Assembler extends AbstractNamespaceApi {
    private static final String SETTINGS_NAME = "settings.properties";

    protected Z80AssemblerSettings settings;

    protected final Map<BigInteger, PostCommandCompiler> postCommandCompilerMap = new LinkedHashMap<>();

    @Getter
    protected BigInteger address = new BigInteger("8000", 16);

    public Z80Assembler() {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        postCommandCompilerMap.clear();
    }

    protected void setSettings(@NonNull Z80AssemblerSettings settings) {
        this.settings = settings;
        setAddress(settings.getDefaultAddress());
    }

    public void run(@NonNull final File... files) {
        for (File file : files) {
            run(file);
        }
    }

    protected void run(@NonNull final File file) {
        OutputStream os;
        final File outputFile = createOutputFile(file);
        try {
            reset();
            os = new FileOutputStream(outputFile);
            final CompilerApi compilerApi = compile(file, os);
            os.close();
            postCompile(outputFile);
            runOptions(outputFile, compilerApi);
            runSettings(outputFile);
            outputCompileResult(compilerApi);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Output.println(e.getMessage());
        }
    }

    protected void runOptions(@NonNull final File outputFile, @NonNull final CompilerApi compilerApi) throws IOException {
        if (compilerApi.hasOption(OptionTypes.PRODUCE_WAV)) {
            final Option option = compilerApi.getOption(OptionTypes.PRODUCE_WAV);
            final Collection<String> paths = (Collection<String>) option.getContent();
            for (String path : paths) {
                createWav(outputFile, new File(path), getAddress());
            }
        }
        if (compilerApi.hasOption(OptionTypes.PRODUCE_TAP)) {
            final Option option = compilerApi.getOption(OptionTypes.PRODUCE_TAP);
            final Collection<String> paths = (Collection<String>) option.getContent();
            for (String path : paths) {
                createTap(outputFile, new File(path), getAddress());
            }
        }
        if (compilerApi.hasOption(OptionTypes.PRODUCE_TZX)) {
            final Option option = compilerApi.getOption(OptionTypes.PRODUCE_TZX);
            final Collection<String> paths = (Collection<String>) option.getContent();
            for (String path : paths) {
                createTzx(outputFile, new File(path), getAddress());
            }
        }
    }

    protected void runSettings(@NonNull final File outputFile) throws IOException {
        if (settings.isProduceWav()) {
            createWav(outputFile, getAddress());
        }
        if (settings.isProduceTap()) {
            createTap(outputFile, getAddress());
        }
        if (settings.isProduceTzx()) {
            createTzx(outputFile, getAddress());
        }
    }

    protected void createWav(@NonNull final File srcFile, @NonNull final BigInteger address) throws IOException {
        final File wavFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), srcFile, WavFile.EXTENSION);
        createWav(srcFile, wavFile, address);
    }

    protected void createWav(@NonNull final File srcFile, @NonNull final File dstFile, @NonNull final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(srcFile);
        final TapData tapData = TapUtil.createBinaryTap(data, address.intValue());
        final SignalGenerator sg = new SignalGenerator(dstFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tapData);
    }

    private TapData createTap(final File srcFile, final BigInteger address) throws IOException {
        final File tapFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), srcFile, TapUtil.EXTENSION);
        return createTap(srcFile, tapFile, address);
    }

    private TapData createTap(final File srcFile, final File dstFile, final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(srcFile);
        return TapUtil.createBinaryTap(dstFile, data, address.intValue());
    }

    private void createTzx(File srcFile, final BigInteger address) throws IOException {
        final File tzxFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), srcFile, TzxUtils.EXTENSION);
        createTzx(srcFile, tzxFile, address);
    }

    private void createTzx(File srcFile, final File dstFile, final BigInteger address) throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(srcFile);
        TzxUtils.createTzx(dstFile, data, address.intValue());
    }

    private File createOutputFile(final File file) {
        if (!settings.getOutputDirectory().exists()) {
            settings.getOutputDirectory().mkdirs();
        }
        return FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, null);
    }

    protected CompilerApi compile(@NonNull final File file, @NonNull final OutputStream os) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return compile(file, fis, os);
        }
    }

    protected CompilerApi compile(@NonNull final File file, @NonNull final InputStream is
            , @NonNull final OutputStream os) throws IOException {
        final CompilerApi compilerApi = CompilerFactory.create((namespaceApi, settingsApi, syntaxAnalyzer, outputStream)
                        -> new Z80Compiler(namespaceApi, settingsApi, syntaxAnalyzer, outputStream)
                , this, settings, file, is, os);
        compilerApi.compile();
        return compilerApi;
    }

    protected String createWelcome() {
        final StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(Z80Messages.getMessage(Z80Messages.PROGRAM_WELCOME)
                , settings.getMajorVersion(), settings.getMinorVersion());
        String writtenBy = Z80Messages.getMessage(Z80Messages.WRITTEN_BY);
        String lineExternal = StringUtils.repeat('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(StringUtils.repeat(' ', 78))
                .append('*')).toString();
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
                , programWelcome)).append(System.lineSeparator());
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2, writtenBy))
                .append(System.lineSeparator());
        sb.append(lineExternal).append(System.lineSeparator());
        return sb.toString();
    }

    protected List<File> setCli(@NonNull final String[] args, @NonNull final Options options) {
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
    public void addToQueue(@NonNull final PostCommandCompiler postCommandCompiler) {
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

    public static void main(final String[] args) {
        final Z80AssemblerSettings settings = loadSettings();
        final Options options = getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(settings.getCmdFilename() + " " + Z80Messages.getMessage(Z80Messages
                    .FILE_ENUM), options);
            return;
        }
        final Z80Assembler z80Assembler = new Z80Assembler();
        z80Assembler.setSettings(settings);
        final List<File> fileList = z80Assembler.setCli(args, options);
        if (!fileList.isEmpty()) {
            Output.println(z80Assembler.createWelcome());
            z80Assembler.run(fileList.toArray(new File[fileList.size()]));
        } else {
            Output.println(Z80Messages.getMessage(Z80Messages.NO_INPUT_FILES));
        }
    }

    protected static Z80AssemblerSettings loadSettings() {
        Z80AssemblerSettings settings = new Z80AssemblerSettings();
        settings.merge(new DefaultSettings());
        try {
            final ResourceSettings resourceSettings = new ResourceSettings();
            resourceSettings.load(SETTINGS_NAME);
            settings.merge(resourceSettings);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return settings;
    }

    protected static Options getOptions() {
        final Options options = new Options();
        options.addOption("st", "strict-type-conversion", false, Z80Messages.getMessage(Z80Messages
                .O_STRICT_CONVERSION));
        options.addOption("a", "address", true, Z80Messages.getMessage(Z80Messages.O_ORG_ADDRESS));
        options.addOption("min", "min-address", true, Z80Messages.getMessage(Z80Messages
                .O_MINIMAL_ADDRESS));
        options.addOption("max", "max-address", true, Z80Messages.getMessage(Z80Messages
                .O_MAXIMAL_ADDRESS));
        options.addOption("o", "output", true, Z80Messages.getMessage(Z80Messages.O_OUTPUT_DIRECTORY));
        options.addOption("s", "source-encoding", true, Z80Messages.getMessage(Z80Messages
                .O_SOURCE_ENCODING));
        options.addOption("tap", false, Z80Messages.getMessage(Z80Messages.O_PRODUCE_TAP));
        options.addOption("wav", false, Z80Messages.getMessage(Z80Messages.O_PRODUCE_WAV));
        options.addOption("tzx", false, Z80Messages.getMessage(Z80Messages.O_PRODUCE_TZX));
        options.addOption("cpu", true, Z80Messages.getMessage(Z80Messages.O_USE_SPECIAL_PROCESSOR));
        return options;
    }

    protected static void outputCompileResult(@NonNull final CompilerApi compilerApi) {
        Output.formatPrintln("%d %s", Output.getWarningCount(), MessageList.getMessage(MessageList.N_WARNINGS));
        Output.formatPrintln("%s %s %d %s, %d %s", MessageList.getMessage(MessageList.COMPILED1)
                , MessageList.getMessage(MessageList.SUCCESSFULLY), compilerApi.getCompiledLineCount()
                , MessageList.getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount()
                , MessageList.getMessage(MessageList.SOURCES));
    }
}