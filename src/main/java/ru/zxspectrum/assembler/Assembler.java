package ru.zxspectrum.assembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.resource.Loader;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
import ru.zxspectrum.assembler.util.FileUtil;
import ru.zxspectrum.assembler.util.SymbolUtils;
import ru.zxspectrum.assembler.util.TypeUtil;
import ru.zxspectrum.io.DuplicateOutputStream;
import ru.zxspectrum.io.tap.TapData;
import ru.zxspectrum.io.tap.TapUtil;
import ru.zxspectrum.io.wav.SoundGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Maxim Gorin
 */

@Slf4j
public class Assembler implements NamespaceApi, SettingsApi {
    protected static final String EXT_TAP = "tap";

    protected static final String EXT_WAV = "wav";

    private static BigInteger currentCodeOffset;

    private final Map<String, LabelInfo> labelMap = new HashMap<>();

    private final Map<String, BigInteger> variableMap = new HashMap<>();

    private final List<PostCommandCompiler> postCommandCompilerList = new LinkedList<>();

    private final Set<File> compiledFileSet = new HashSet<>();

    private final static Args ARGS = new Args();

    public Assembler() {
        reset();
        loadSettings();
    }

    private void reset() {
        currentCodeOffset = BigInteger.ZERO;
        postCommandCompilerList.clear();
        variableMap.clear();
        labelMap.clear();
    }

    private void loadSettings() {
        try {
            Variables.load(Loader.openRoot("settings.properties"));
            ARGS.setPlatformEncoding(Encoding.valueByName(
                    Variables.getString(Variables.PLATFORM_ENCODING, Encoding
                            .ASCII.getName())));
            ARGS.setSourceEncoding(Encoding.valueByName(Variables.getString(Variables.SOURCE_ENCODING, Encoding
                    .UTF_8.getName())));
            String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
            ARGS.setByteOrder("big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian);
            ARGS.setMinAddress(Variables.getBigInteger(Variables.MIN_ADDRESS, ARGS.getMinAddress()));
            ARGS.setMaxAddress(Variables.getBigInteger(Variables.MAX_ADDRESS, ARGS.getMaxAddress()));
            ARGS.setOutputDirectory(new File(Variables.getString(Variables.OUTPUT_DIRECTORY, "output")));
            ARGS.setMajorVersion(Variables.getString(Variables.MAJOR_VERSION, ARGS.getMajorVersion()));
            ARGS.setMinorVersion(Variables.getString(Variables.MINOR_VERSION, ARGS.getMinorVersion()));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void run(@NonNull File... files) throws IOException {
        OutputStream os = null;
        ByteArrayOutputStream baos = null;
        try {
            for (File file : files) {
                try {
                    reset();
                    File outputFile = createOutputFile(file);
                    if (ARGS.isProduceTap() || ARGS.isProduceWav()) {
                        os = new DuplicateOutputStream(new FileOutputStream(outputFile)
                                , baos = new ByteArrayOutputStream());
                    } else {
                        os = new FileOutputStream(outputFile);
                    }
                    final CompilerApi compilerApi = runSingle(file, os);
                    postCompile(outputFile);
                    Output.formatPrintln("%d %s", Output.getWarningCount(),
                            MessageList.getMessage(MessageList.N_WARNINGS));
                    Output.formatPrintln("%s %s %d %s, %d %s", MessageList.getMessage(MessageList.COMPILED1),
                            MessageList.getMessage(MessageList.SUCCESSFULLY), compilerApi.getCompiledLineCount(),
                            MessageList
                                    .getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount(), MessageList
                                    .getMessage(MessageList.SOURCES));
                    if (ARGS.isProduceTap()) {
                        createTap(file, baos.toByteArray(), getAddress());
                    }
                    if (ARGS.isProduceWav()) {
                        createWav(file, baos.toByteArray(), getAddress());
                    }

                } finally {
                    FileUtil.safeClose(os);
                }
            }
        } catch (Exception e) {
            Output.println(e.getMessage());
            log.error(e.getMessage(), e);
        }
    }

    private void createWav(File file, @NonNull final byte[] data, @NonNull final BigInteger address)
            throws IOException {
        final TapData tapData = TapUtil.createBinaryTap(data, address.intValue());
        final File wavFile = FileUtil.createNewFileSameName(ARGS.getOutputDirectory(), file, EXT_WAV);
        final SoundGenerator sg = new SoundGenerator(wavFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tapData);
    }

    private TapData createTap(final File file, @NonNull final byte[] data,
                              @NonNull final BigInteger address)
            throws IOException {
        final File tapFile = FileUtil.createNewFileSameName(ARGS.getOutputDirectory(), file, EXT_TAP);
        return TapUtil.createBinaryTap(tapFile, data, address.intValue());
    }

    private File createOutputFile(File file) {
        if (!ARGS.getOutputDirectory().exists()) {
            ARGS.getOutputDirectory().mkdirs();
        }
        return FileUtil.createNewFileSameName(ARGS.getOutputDirectory(), file, null);
    }

    protected CompilerApi runSingle(File file, OutputStream os) throws IOException {
        CompilerApi compilerApi = CompilerFactory.create(this, this, file, os);
        compilerApi.compile();
        return compilerApi;
    }

    private static String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(MessageList.getMessage(MessageList.PROGRAM_WELCOME),
                ARGS.getMajorVersion()
                , ARGS.getMinorVersion());
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

    public static void main(String[] args) throws Exception {
        Options options = getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("z80asm <file1>...<fileN>", options);
            return;
        }
        List<File> fileList = new LinkedList<>();
        for (String arg : cliParsing(args, options)) {
            fileList.add(new File(arg));
        }
        Output.println(createWelcome());
        Assembler assembler = new Assembler();
        assembler.run(fileList.toArray(new File[fileList.size()]));
    }

    private static Options getOptions() {
        Options options = new Options();
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

    private static List<String> cliParsing(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("a")) {
                ARGS.setAddress(new BigInteger(cli.getOptionValue("a")));
            }
            if (cli.hasOption("min")) {
                ARGS.setMinAddress(new BigInteger(cli.getOptionValue("min")));
            }
            if (cli.hasOption("max")) {
                ARGS.setMaxAddress(new BigInteger(cli.getOptionValue("max")));
            }
            if (cli.hasOption("o")) {
                ARGS.setOutputDirectory(new File(cli.getOptionValue("o")));
            }
            if (cli.hasOption("b")) {
                ARGS.setByteOrder("big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                        ByteOrder.LittleEndian);
            }
            if (cli.hasOption("s")) {
                ARGS.setSourceEncoding(Encoding.valueByName(cli.getOptionValue("s")));
            }
            if (cli.hasOption("p")) {
                ARGS.setPlatformEncoding(Encoding.valueByName(cli.getOptionValue("p")));
            }
            if (cli.hasOption("tap")) {
                ARGS.setProduceTap(true);
            }

            if (cli.hasOption("wav")) {
                ARGS.setProduceWav(true);
            }

            return cli.getArgList();
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return Collections.emptyList();
    }
    //----------------------------------------------------------

    @Override
    public boolean containsLabel(String labelName) {
        return labelMap.containsKey(labelName);
    }

    @Override
    public void putLabel(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        labelMap.put(name, new LabelInfo(currentCodeOffset));
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName, boolean used) {
        LabelInfo labelInfo = labelMap.get(labelName);
        if (labelInfo == null) {
            return BigInteger.valueOf(-1);
        }
        labelInfo.setUsed(used);
        return labelInfo.getCodeOffset();
    }

    @Override
    public BigInteger getLabelCodeOffset(String labelName) {
        LabelInfo labelInfo = labelMap.get(labelName);
        if (labelInfo == null) {
            return BigInteger.valueOf(-1);
        }
        return labelInfo.getCodeOffset();
    }

    @Override
    public BigInteger getCurrentCodeOffset() {
        return currentCodeOffset;
    }

    @Override
    public BigInteger incCurrentCodeOffset(BigInteger delta) {
        if (delta.signum() == -1) {
            throw new IllegalArgumentException("delta is negative");
        }
        if (delta == null) {
            throw new NullPointerException("delta");
        }
        currentCodeOffset = currentCodeOffset.add(delta);
        return currentCodeOffset;
    }

    @Override
    public void setAddress(BigInteger address) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        if (address.signum() == -1) {
            throw new IllegalArgumentException("address is negative");
        }
        if (!TypeUtil.isInRange(ARGS.getMinAddress(), ARGS.getMaxAddress(), address)) {
            throw new IllegalArgumentException("address is out of range");
        }
        ARGS.setAddress(address);
    }

    @Override
    public BigInteger getAddress() {
        return ARGS.getAddress();
    }

    @Override
    public void addToList(@NonNull PostCommandCompiler postCommandCompiler) {
        postCommandCompilerList.add(postCommandCompiler);
    }

    @Override
    public boolean isCompiled(File file) {
        if (file == null) {
            return false;
        }
        return compiledFileSet.contains(file);
    }

    @Override
    public boolean addCompiled(File file) {
        if (file == null) {
            return false;
        }
        return compiledFileSet.add(file);
    }

    @Override
    public boolean containsVariable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return variableMap.containsKey(name);
    }

    @Override
    public BigInteger getVariableValue(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return variableMap.get(name);
    }

    @Override
    public BigInteger addVariable(String name, BigInteger value) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        if (value == null) {
            return null;
        }
        return variableMap.put(name, value);
    }

    @Override
    public boolean removeVariable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return variableMap.remove(name) != null;
    }

    protected void postCompile(File outputFile) {
        RandomAccessFile randomAccessFile = null;
        try {
            Collections.sort(postCommandCompilerList,
                    (o1, o2) -> o1.getCommandOffset().compareTo(o2.getCommandOffset()));
            randomAccessFile = new RandomAccessFile(outputFile, "rwd");
            for (PostCommandCompiler postCommandCompiler : postCommandCompilerList) {
                postCommandCompiler.compile(randomAccessFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            FileUtil.safeClose(randomAccessFile);
        }
    }

    @Override
    public ByteOrder getByteOrder() {
        return ARGS.getByteOrder();
    }

    @Override
    public Encoding getSourceEncoding() {
        return ARGS.getSourceEncoding();
    }

    @Override
    public Encoding getPlatformEncoding() {
        return ARGS.getPlatformEncoding();
    }

    @Override
    public BigInteger getMinAddress() {
        return ARGS.getMinAddress();
    }

    @Override
    public BigInteger getMaxAddress() {
        return ARGS.getMaxAddress();
    }

    @Override
    public File getOutputDirectory() {
        return ARGS.getOutputDirectory();
    }

    static class LabelInfo {

        BigInteger codeOffset;

        boolean used;

        LabelInfo(BigInteger codeOffset) {
            this.codeOffset = codeOffset;
        }

        BigInteger getCodeOffset() {
            return codeOffset;
        }

        boolean isUsed() {
            return used;
        }

        void setUsed(boolean used) {
            this.used = used;
        }
    }
}