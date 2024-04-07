package ru.assembler.microsha;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.CompilerFactory;
import ru.assembler.core.compiler.PostCommandCompiler;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.error.SettingsException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.ErrorOutput;
import ru.assembler.core.io.FileDescriptor;
import ru.assembler.core.io.LimitedOutputStream;
import ru.assembler.core.io.Output;
import ru.assembler.core.ns.AbstractNamespaceApi;
import ru.assembler.core.settings.ResourceSettings;
import ru.assembler.core.util.FileUtil;
import ru.assembler.core.util.SymbolUtil;
import ru.assembler.core.util.TypeUtil;
import ru.assembler.microsha.core.compiler.MicroshaCompiler;
import ru.assembler.microsha.core.compiler.option.OptionTypes;
import ru.assembler.microsha.core.settings.DefaultSettings;
import ru.assembler.microsha.core.settings.MicroshaAssemblerSettings;
import ru.assembler.microsha.io.generator.SoundGenerator;
import ru.assembler.microsha.io.rkm.RkmData;
import ru.assembler.microsha.text.MicroshaMessages;
import ru.zxspectrum.io.audio.wav.WavWriter;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Maxim Gorin
 */

@Slf4j
public class MicroshaAssembler extends AbstractNamespaceApi {
    private static final String SETTINGS_NAME = "asm/settings.properties";

    protected MicroshaAssemblerSettings settings;

    protected final Map<BigInteger, PostCommandCompiler> postCommandCompilerMap = new LinkedHashMap<>();

    @Getter
    protected BigInteger address = BigInteger.valueOf(0);

    @Setter(AccessLevel.PROTECTED)
    @Getter
    @NonNull
    protected BigInteger minAddress = BigInteger.valueOf(0);

    @Setter(AccessLevel.PROTECTED)
    @Getter
    @NonNull
    protected BigInteger maxAddress = BigInteger.valueOf(32767);

    public MicroshaAssembler() {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        postCommandCompilerMap.clear();
    }

    protected void applySettings(@NonNull final MicroshaAssemblerSettings settings) throws SettingsException {
        if (settings.getMinAddress() != null) {
            if (!TypeUtil.isInRange(BigInteger.ZERO, BigInteger.valueOf(0xffff),
                    settings.getMinAddress())) {
                throw new SettingsException(
                        String.format(MicroshaMessages.getMessage(MicroshaMessages.MIN_ADDRESS_OUT_OF_RANGE)
                                , settings.getMinAddress()));
            }
            setMinAddress(settings.getMinAddress());
        }
        if (settings.getMaxAddress() != null) {
            if (!TypeUtil.isInRange(BigInteger.ZERO, BigInteger.valueOf(0xffff),
                    settings.getMaxAddress())) {
                throw new SettingsException(
                        String.format(MicroshaMessages.getMessage(MicroshaMessages.MAX_ADDRESS_OUT_OF_RANGE)
                                , settings.getMaxAddress()));
            }
            setMaxAddress(settings.getMaxAddress());
        }
        if (settings.getMinAddress().compareTo(settings.getMaxAddress()) >= 0) {
            throw new SettingsException(
                    String.format(MicroshaMessages.getMessage(MicroshaMessages.MIN_ADDRESS_GREATER_OR_EQUAL)
                            , settings.getMinAddress(), settings.getMaxAddress()));
        }
        if (settings.getDefaultAddress() != null) {
            setAddress(settings.getDefaultAddress());
        }
        this.settings = settings;
    }

    public void run(@NonNull final FileDescriptor... fds) {
        for (FileDescriptor fd : fds) {
            run(fd);
        }
    }

    protected void run(@NonNull final FileDescriptor fd) {
        OutputStream os;
        final File outputFile = createOutputFile(fd.getFile());
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            log.error("Can not create {}", outputFile);
            ErrorOutput.formatPrintln(MicroshaMessages.getMessage(MicroshaMessages.CANT_CREATE_FILE), outputFile.getAbsolutePath());
            return;
        }
        try {
            reset();
            os = new FileOutputStream(outputFile);
            final CompilerApi compilerApi = compile(fd, os);
            os.close();
            postCompile(outputFile);
            Output.formatPrintln("%s %s", MicroshaMessages.getMessage(MicroshaMessages.COMPILED_FILE_IN), outputFile);
            runOptions(outputFile, compilerApi);
            runSettings(outputFile);
            outputCompileResult(compilerApi);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorOutput.println(e.getMessage());
        }
    }

    protected void runOptions(@NonNull final File outputFile, @NonNull final CompilerApi compilerApi) throws IOException {
        if (compilerApi.hasOption(OptionTypes.PRODUCE_WAV)) {
            final Option option = compilerApi.getOption(OptionTypes.PRODUCE_WAV);
            final Collection<String> paths = (Collection<String>) option.getContent();
            for (String path : paths) {
                final File wavFile = new File(path);
                createWav(outputFile, wavFile, getAddress());
                Output.formatPrintln("%s %s", MicroshaMessages.getMessage(MicroshaMessages.SAVED_FILE_IN), wavFile);
            }
        }
        if (compilerApi.hasOption(OptionTypes.PRODUCE_RKM)) {
            final Option option = compilerApi.getOption(OptionTypes.PRODUCE_RKM);
            final Collection<String> paths = (Collection<String>) option.getContent();
            for (String path : paths) {
                final File rkmFile = new File(path);
                createRkm(outputFile, rkmFile, getAddress());
                Output.formatPrintln("%s %s", MicroshaMessages.getMessage(MicroshaMessages.SAVED_FILE_IN), rkmFile);
            }
        }
    }

    protected void runSettings(@NonNull final File outputFile) throws IOException {
        if (settings.isProduceWav()) {
            final File wavFile = createWav(outputFile, getAddress());
            Output.formatPrintln("%s %s", MicroshaMessages.getMessage(MicroshaMessages.SAVED_FILE_IN), wavFile);
        }
        if (settings.isProduceRkm()) {
            final File rkmFile = createRkmWithNoResult(outputFile, getAddress());
            Output.formatPrintln("%s %s", MicroshaMessages.getMessage(MicroshaMessages.SAVED_FILE_IN), rkmFile);
        }
    }

    protected File createWav(@NonNull final File file, @NonNull final BigInteger address) throws IOException {
        final File wavFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, WavWriter.EXTENSION);
        createWav(file, wavFile, address);
        return wavFile;
    }

    protected void createWav(@NonNull final File src, @NonNull final File dst, @NonNull final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(src);
        final RkmData rkm = new RkmData(address.intValue(), data);
        final SoundGenerator sg = new SoundGenerator(dst);
        sg.generateWav(rkm);
    }

    private RkmData createRkm(final File file, @NonNull final BigInteger address) throws IOException {
        final File rkmFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, RkmData.EXTENSION);
        return createRkm(file, rkmFile, address);
    }

    private File createRkmWithNoResult(final File file, @NonNull final BigInteger address) throws IOException {
        final File rkmFile = FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, RkmData.EXTENSION);
        createRkm(file, rkmFile, address);
        return rkmFile;
    }

    private RkmData createRkm(@NonNull final File src, @NonNull final File dst, @NonNull final BigInteger address)
            throws IOException {
        final byte[] data = FileUtils.readFileToByteArray(src);
        RkmData rkmData = new RkmData(address.intValue(), data);
        try (FileOutputStream fos = new FileOutputStream(dst)) {
            rkmData.save(fos);
            return rkmData;
        }
    }

    private File createOutputFile(final File file) {
        if (!settings.getOutputDirectory().exists()) {
            settings.getOutputDirectory().mkdirs();
        }
        return FileUtil.createNewFileSameName(settings.getOutputDirectory(), file, null);
    }

    protected CompilerApi compile(@NonNull final FileDescriptor fd, @NonNull final OutputStream os) throws IOException {
        try (FileInputStream fis = new FileInputStream(fd.getFile())) {
            return compile(fd, fis, os);
        }
    }

    protected CompilerApi compile(@NonNull final FileDescriptor fd, @NonNull final InputStream is
            , @NonNull final OutputStream os) throws IOException {
        final LimitedOutputStream los = new LimitedOutputStream(os, () -> settings.getMaxAddress().subtract(
                getAddress()).intValue());
        final CompilerApi compilerApi = CompilerFactory.create((namespaceApi, settingsApi, syntaxAnalyzer, outputStream)
                        -> new MicroshaCompiler(namespaceApi, settingsApi, syntaxAnalyzer, outputStream)
                , this, settings, fd, is, los);
        compilerApi.compile();
        return compilerApi;
    }

    protected String createWelcome() {
        final StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(MicroshaMessages.getMessage(MicroshaMessages.PROGRAM_WELCOME)
                , settings.getMajorVersion(), settings.getMinorVersion());
        String writtenBy = MicroshaMessages.getMessage(MicroshaMessages.WRITTEN_BY);
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

    private static FileDescriptor toFileDescription(String filename) {
        final String[] pair = filename.split("#");
        return pair.length == 2 ? new FileDescriptor(new File(pair[0]), pair[1]) :
                new FileDescriptor(new File(pair[0]));
    }

    protected static List<FileDescriptor> setCli(@NonNull MicroshaAssemblerSettings settings, @NonNull final String[] args
            , @NonNull final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            final CommandLine cli = parser.parse(options, args);
            settings.load(cli);
            final List<FileDescriptor> files = new LinkedList<>();
            for (final String fileName : cli.getArgList()) {
                files.add(toFileDescription(fileName));
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
        if (!TypeUtil.isInRange(getMinAddress(), getMaxAddress(), address)) {
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

    protected static MicroshaAssemblerSettings loadSettings() {
        final MicroshaAssemblerSettings settings = new MicroshaAssemblerSettings();
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
        options.addOption("st", "strict-type-conversion", false, MicroshaMessages
                .getMessage(MicroshaMessages.O_STRICT_CONVERSION));
        options.addOption("a", "address", true, MicroshaMessages
                .getMessage(MicroshaMessages.O_ORG_ADDRESS));
        options.addOption("min", "min-address", true, MicroshaMessages.getMessage(MicroshaMessages
                .O_MINIMAL_ADDRESS));
        options.addOption("max", "max-address", true, MicroshaMessages.getMessage(MicroshaMessages
                .O_MAXIMAL_ADDRESS));
        options.addOption("o", "output", true, MicroshaMessages.getMessage(MicroshaMessages
                .O_OUTPUT_DIRECTORY));
        options.addOption("s", "source-encoding", true, MicroshaMessages.getMessage(MicroshaMessages
                .O_SOURCE_ENCODING));
        options.addOption("rkm", false, MicroshaMessages.getMessage(MicroshaMessages.O_PRODUCE_RKM));
        options.addOption("wav", false, MicroshaMessages.getMessage(MicroshaMessages.O_PRODUCE_WAV));
        options.addOption("cpu", true, MicroshaMessages.getMessage(MicroshaMessages.O_USE_SPECIAL_PROCESSOR));
        return options;
    }

    protected static void outputCompileResult(@NonNull CompilerApi compilerApi) {
        Output.formatPrintln("%d %s", Output.getWarningCount(), Messages.getMessage(Messages.N_WARNINGS));
        Output.formatPrintln("%s %s %d %s, %d %s", Messages.getMessage(Messages.COMPILED1)
                , Messages.getMessage(Messages.SUCCESSFULLY), compilerApi.getCompiledLineCount()
                , Messages.getMessage(Messages.LINES), compilerApi.getCompiledSourceCount()
                , Messages.getMessage(Messages.SOURCES));
    }

    public static void main(final String[] args) {
        final MicroshaAssemblerSettings settings = loadSettings();
        final Options options = getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(settings.getCmdFilename() + " " + MicroshaMessages.getMessage(MicroshaMessages
                    .FILE_ENUM), options);
            return;
        }
        final MicroshaAssembler asm = new MicroshaAssembler();
        final List<FileDescriptor> fdList = setCli(settings, args, options);
        try {
            asm.applySettings(settings);
        } catch (SettingsException e) {
            ErrorOutput.println(e.getMessage());
            return;
        }
        if (!fdList.isEmpty()) {
            Output.println(asm.createWelcome());
            asm.run(fdList.toArray(new FileDescriptor[fdList.size()]));
        } else {
            Output.println(MicroshaMessages.getMessage(MicroshaMessages.NO_INPUT_FILES));
        }
    }

    public static void entry(@NonNull Collection<String> c) {
        main(c.toArray(new String[c.size()]));
    }
}
