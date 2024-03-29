package ru.zxspectrum.disassembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import ru.zxspectrum.disassembler.command.CommandRecord;
import ru.zxspectrum.disassembler.concurrent.DecoderExecutor;
import ru.zxspectrum.disassembler.decode.Decoder;
import ru.zxspectrum.disassembler.enrich.Enricher;
import ru.zxspectrum.disassembler.error.ExceptionGroup;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.ByteCodeInputStream;
import ru.zxspectrum.disassembler.io.ErrorOutput;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.lang.tree.Tree;
import ru.zxspectrum.disassembler.loader.DisassemblerLoader;
import ru.zxspectrum.disassembler.render.Number;
import ru.zxspectrum.disassembler.render.*;
import ru.zxspectrum.disassembler.render.command.CommandFactory;
import ru.zxspectrum.disassembler.render.system.Org;
import ru.zxspectrum.disassembler.render.system.Z80;
import ru.zxspectrum.disassembler.settings.DefaultSettings;
import ru.zxspectrum.disassembler.settings.DisassemblerSettings;
import ru.zxspectrum.disassembler.sys.Environment;
import ru.zxspectrum.disassembler.utils.FileUtils;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

/**
 * @author Maxim Gorin
 * Date: 12/24/2023
 */
@Slf4j
public class Disassembler implements Environment {
    protected static final String EXT = "asm";

    private final Map<BigInteger, String> labelMap = new HashMap<>();

    private static final Tree<CommandFactory> TREE = new Tree<>();

    private DisassemblerSettings settings;

    private int errorCount;

    private static int addressDimension;

    private List<File> successfullyDisassembled = new LinkedList<>();

    private Disassembler() {
    }

    public Disassembler(@NonNull DisassemblerSettings settings) {
        setSettings(settings);
    }

    protected void reset() {
        errorCount = 0;
        addressDimension = 0;
        labelMap.clear();
        successfullyDisassembled.clear();
    }

    protected void setSettings(@NonNull DisassemblerSettings settings) {
        this.settings = settings;
        addressDimension = Type.getSize(settings.getMaxAddress().longValue());
        if (addressDimension <= 0) {
            throw new IllegalArgumentException("Bad address size");
        }
    }

    protected Collection<String> getCli(@NonNull final String[] args, @NonNull final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine cli = parser.parse(options, args);
            settings.load(cli);
            applySettings();
            return cli.getArgList();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private void applySettings() {
        Address.setVisible(settings.isAddressVisible());
        Number.setRadix(settings.getRadix());
        Number.setStyle(settings.getNumberStyle());
        Cell.setUppercase(settings.isUpperCase());
        Decoder.setStrategy(settings.getStrategy());
    }

    public void run(@NonNull final File... files) {
        if (!settings.getStdout()) {
            Output.println(createWelcome());
        }
        for (final File file : files) {
            try {
                runSingle(file);
                successfullyDisassembled.add(file);
            } catch (UnsupportedCharsetException | RenderException | InterruptedException | IOException e) {
                log.error(e.getMessage(), e);
                ErrorOutput.println(file, e.getMessage());
                errorCount++;
            } catch (ExceptionGroup e) {
                log.error(e.getMessage(), e);
                for (Throwable t : e.getExceptions()) {
                    ErrorOutput.println(e.getMessage());
                }
                errorCount += e.getExceptions().size();
            }
        }
        if (!settings.getStdout()) {
            showTotalReport();
        }
    }

    private void runSingle(final File file) throws IOException, InterruptedException, RenderException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format(Messages.getMessage(Messages
                    .FILE_NOT_FOUND), file.getName()));
        }
        final ByteCodeInputStream is = new ByteCodeInputStream(file, settings.getByteOrder());
        final DecoderExecutor executor = new DecoderExecutor();
        final Decoder decoder = new Decoder(executor, TREE, settings.getDefaultAddress(), is);
        final Canvas canvas = new Canvas();
        canvas.setEncoding(settings.getEncoding());
        decoder.setCanvas(canvas);
        executor.execute(decoder);
        executor.await();
        executor.shutdown();
        final Collection<Throwable> errors = executor.getErrors();
        if (!errors.isEmpty()) {
            throw new ExceptionGroup(errors);
        }
        enrichSystems(decoder, canvas);
        Enricher.enrichLabels(this, canvas);
        enrichComment(canvas);
        if (settings.getStdout()) {
            canvas.flush(System.out);
        } else {
            final File outputFile = FileUtils.createNewFileSameName(new File("."),
                    file.getAbsoluteFile(), EXT);
            canvas.setFile(outputFile);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                canvas.flush(fos);
            }
            showReport(canvas);
        }
    }

    private void enrichSystems(@NonNull Decoder decoder, @NonNull Canvas canvas) {
        canvas.setOrg(RowFactory.createRow(new Org(decoder.getFirstAddress())));
        canvas.setZ80(RowFactory.createRow(new Z80()));
    }

    private void enrichComment(@NonNull final Canvas canvas) {
        if (settings.hasComments()) {
            Enricher.enrichComment(this, canvas);
        }
    }

    private void showReport(Canvas canvas) {
        final StringBuilder sb = new StringBuilder();
        sb.append(Messages.getMessage(Messages.DISASSEMBLED_LINES_IN));
        Output.println(sb.toString(), String.valueOf(canvas.getLineCount()), canvas.getFile().getName());
    }

    private void showTotalReport() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Messages.getMessage(Messages.TOTAL_ERRORS));
        Output.println(sb.toString(), String.valueOf(errorCount));
        sb.setLength(0);
        sb.append(Messages.getMessage(Messages.SUCCESSFULLY_DISASSEMBLED));
        Output.println(sb.toString(), String.valueOf(successfullyDisassembled.size()));
    }

    private String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(Messages.getMessage(Messages.PROGRAM_WELCOME), settings.getMajorVersion()
                , settings.getMinorVersion());
        String writtenBy = Messages.getMessage(Messages.WRITTEN_BY);
        String lineExternal = StringUtils.repeat('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(StringUtils.repeat(' ', 78))
                .append('*')).toString();
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
                , programWelcome)).append(System.lineSeparator());
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2
                , writtenBy)).append(System.lineSeparator());
        sb.append(lineExternal).append(System.lineSeparator());
        return sb.toString();
    }

    public static void main(final String[] args) {
        try {
            final DisassemblerSettings settings = loadSettings();
            final Disassembler disassembler = new Disassembler(settings);
            loadCommands(settings);
            final Options options = getOptions();
            if (args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(String.format("%s %s...%s", settings.getCmdFilename(), Messages.getMessage(Messages
                        .FILE_ARG1), Messages.getMessage(Messages.FILE_ARGN)), options);
                return;
            }
            final Collection<String> fileNames = disassembler.getCli(args, options);
            if (fileNames.isEmpty()) {
                Output.println(Messages.getMessage(Messages.NO_INPUT_FILES));
                return;
            }
            final List<File> files = new LinkedList<>();
            for (String fileName : fileNames) {
                files.add(new File(fileName));
            }
            disassembler.run(files.toArray(new File[files.size()]));
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            ErrorOutput.println(e.getMessage());
        }
    }

    public static void entry(@NonNull Collection<String> col) {
        main(col.toArray(new String[col.size()]));
    }

    protected static DisassemblerSettings loadSettings() {
        final DisassemblerSettings settings = new DisassemblerSettings();
        settings.merge(new DefaultSettings());
        try {
            settings.load(new ByteArrayInputStream(IOUtils.resourceToByteArray("/settings.properties")));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return settings;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("a", "address", true, "default address." +
                " Non negative value.");
        options.addOption("min", "min-address", true, "minimal address." +
                " Non negative value.");
        options.addOption("max", "max-address", true, "maximal address." +
                " Non negative value.");
        options.addOption("o", "output", true, "output directory for" +
                " disassembled files.");
        options.addOption("b", "byte-order", true, "byte order" +
                ": little-endian or big-endian.");
        options.addOption("v", "visible", false, "Show or not address in"
                + " decompiled file.");
        options.addOption("stdout", "standard-output", false, "Redirect"
                + " result into stdout stream");
        options.addOption("r", "radix", true, "Set radix (bin, oct, dec"
                + ", hex). Hex is" +
                " default");
        options.addOption("lc", "letter-case", true, "Set letter case"
                + " (upper, lower). Upper is default");
        options.addOption("ns", "number-style", true, "Set number style"
                + " (c, java, nix, classic, retro). Classic is default");
        options.addOption("s", "strategy", true, "Set decompiling"
                + " strategy (sequentially, branching). Default is sequentially");
        options.addOption("c", "comments", true, "Switch on or off"
                + " comments. Default is on");
        options.addOption("e", "encoding", true, "Set encoding for result files. Default" +
                " is UTF-8");
        return options;
    }

    protected static void loadCommands(@NonNull final DisassemblerSettings settings) throws IOException {
        final DisassemblerLoader loader = new DisassemblerLoader();
        final Set<CommandRecord> commands = new HashSet<>();
        for (String path : settings.getTemplates()) {
            final InputStream is = Disassembler.class.getResourceAsStream(path);
            if (is != null) {
                commands.addAll(loader.load(is, Charset.defaultCharset()));
            }
            is.close();
        }
        for (final CommandRecord r : commands) {
            TREE.add(r.getCodePattern(), new CommandFactory(r));
        }
    }

    @Override
    public String getExtension() {
        return EXT;
    }

    @Override
    public int getAddressDimension() {
        return addressDimension;
    }

    @Override
    public DisassemblerSettings getSettings() {
        return settings;
    }

    @Override
    public String getLabel(@NonNull BigInteger address) {
        return labelMap.get(address);
    }

    @Override
    public void putLabel(@NonNull BigInteger address, @NonNull String labelName) {
        labelMap.put(address, labelName);
    }
}
