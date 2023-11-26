package ru.zxspectrum.assembler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
import ru.zxspectrum.assembler.util.FileUtil;
import ru.zxspectrum.assembler.util.SymbolUtils;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
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
    private static String majorVersion = "1";

    private static String minorVersion = "1";

    private static BigInteger address = BigInteger.ZERO;

    private static BigInteger currentCodeOffset = BigInteger.ZERO;

    private static Encoding sourceEncoding = Encoding.UTF_8;

    private static Encoding platformEncoding = Encoding.ASCII;

    private static ByteOrder byteOrder = ByteOrder.LittleEndian;

    private static BigInteger minAddress = BigInteger.ZERO;

    private static BigInteger maxAddress = new BigInteger("FFFF", 16);

    private static File outputDirectory = new File("output");

    private final Map<String, LabelInfo> labelMap = new HashMap<>();

    private final Map<String, BigInteger> variableMap = new HashMap<>();

    private final List<PostCommandCompiler> postCommandCompilerList = new LinkedList<>();

    private final Set<File> compiledFileSet = new HashSet<>();

    private static final String EXT = "bin";

    public Assembler() {
        loadSettings();
    }

    private void loadSettings() {
        try {
            Variables.load(Assembler.class.getResourceAsStream("/settings.properties"));
            platformEncoding = Encoding.valueByName(Variables.getString(Variables.PLATFORM_ENCODING, Encoding
                    .ASCII.getName()));
            sourceEncoding = Encoding.valueByName(Variables.getString(Variables.SOURCE_ENCODING, Encoding
                    .UTF_8.getName()));
            String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
            byteOrder = "big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian;
            minAddress = Variables.getBigInteger(Variables.MIN_ADDRESS, minAddress);
            maxAddress = Variables.getBigInteger(Variables.MAX_ADDRESS, maxAddress);
            outputDirectory = new File(Variables.getString(Variables.OUTPUT_DIRECTORY, "output"));
            majorVersion = Variables.getString(Variables.MAJOR_VERSION, majorVersion);
            minorVersion = Variables.getString(Variables.MINOR_VERSION, minorVersion);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void run(File... files) throws IOException {
        FileOutputStream fos = null;
        try {
            for (File file : files) {
                try {
                    File outpuitFile = createOutputFile(file);
                    fos = new FileOutputStream(outpuitFile);
                    CompilerApi compilerApi = runSingle(file, fos);
                    postCompile(outpuitFile);
                    Output.formatPrintln("%d %s", Output.getWarningCount(), MessageList.getMessage(MessageList.N_WARNINGS));
                    Output.formatPrintln("%s %s %d %s, %d %s", MessageList.getMessage(MessageList.COMPILED1),
                            MessageList.getMessage(MessageList.SUCCESSFULLY), compilerApi.getCompiledLineCount(), MessageList
                                    .getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount(), MessageList
                                    .getMessage(MessageList.SOURCES));
                } finally {
                    if (fos == null) {
                        try {
                            fos.close();
                            fos = null;
                        } catch (Exception e) {
                            log.debug(e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Output.println(e.getMessage());
            log.debug(e.getMessage());
        }
    }

    private File createOutputFile(File file) throws IOException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        return FileUtil.createNewFileSameName(outputDirectory, file, EXT);
    }

    protected CompilerApi runSingle(File file, OutputStream os) throws IOException {
        CompilerApi compilerApi = CompilerFactory.create(this, this, file, os);
        compilerApi.compile();
        return compilerApi;
    }

    private static String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(MessageList.getMessage(MessageList.PROGRAM_WELCOME), majorVersion
                , minorVersion);
        String writtenBy = MessageList.getMessage(MessageList.WRITTEN_BY);
        String lineExternal = SymbolUtils.fillChar('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(SymbolUtils.fillChar(' ', 78))
                .append('*')).toString();
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
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
        return options;
    }

    private static List<String> cliParsing(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("a")) {
                address = new BigInteger(cli.getOptionValue("a"));
            }
            if (cli.hasOption("min")) {
                minAddress = new BigInteger(cli.getOptionValue("min"));
            }
            if (cli.hasOption("max")) {
                minAddress = new BigInteger(cli.getOptionValue("max"));
            }
            if (cli.hasOption("o")) {
                outputDirectory = new File(cli.getOptionValue("o"));
            }
            if (cli.hasOption("b")) {
                byteOrder = "big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                        ByteOrder.LittleEndian;
            }
            if (cli.hasOption("s")) {
                sourceEncoding = Encoding.valueByName(cli.getOptionValue("s"));
            }
            if (cli.hasOption("p")) {
                platformEncoding = Encoding.valueByName(cli.getOptionValue("p"));
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
        if (!TypeUtil.isInRange(minAddress, maxAddress, address)) {
            throw new IllegalArgumentException("address is out of range");
        }
        this.address = address;
    }

    @Override
    public BigInteger getAddress() {
        return address;
    }

    @Override
    public void addToList(PostCommandCompiler postCommandCompiler) {
        if (postCommandCompiler == null) {
            throw new NullPointerException("commandCompiler");
        }
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
        if (name == null || name.trim().isEmpty())
            return false;
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

    protected void postCompile(File outputFile) {
        RandomAccessFile randomAccessFile = null;
        try {
            Collections.sort(postCommandCompilerList, new Comparator<PostCommandCompiler>() {
                @Override
                public int compare(PostCommandCompiler o1, PostCommandCompiler o2) {
                    return o1.getCommandOffset().compareTo(o2.getCommandOffset());
                }
            });
            randomAccessFile = new RandomAccessFile(outputFile, "rwd");
            for (PostCommandCompiler postCommandCompiler : postCommandCompilerList) {
                postCommandCompiler.compile(randomAccessFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postCommandCompilerList.clear();
            labelMap.clear();
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public Encoding getSourceEncoding() {
        return sourceEncoding;
    }

    @Override
    public Encoding getPlatformEncoding() {
        return platformEncoding;
    }

    @Override
    public BigInteger getMinAddress() {
        return minAddress;
    }

    @Override
    public BigInteger getMaxAddress() {
        return maxAddress;
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
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