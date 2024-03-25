package ru.zxspectrum.disassembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.command.CommandRecord;
import ru.zxspectrum.disassembler.concurrent.DecoderExecutor;
import ru.zxspectrum.disassembler.decode.Decoder;
import ru.zxspectrum.disassembler.error.ExceptionGroup;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.ByteCodeInputStream;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.lang.tree.Tree;
import ru.zxspectrum.disassembler.loader.DisassemblerLoader;
import ru.zxspectrum.disassembler.render.Canvas;
import ru.zxspectrum.disassembler.render.Label;
import ru.zxspectrum.disassembler.render.Row;
import ru.zxspectrum.disassembler.render.RowFactory;
import ru.zxspectrum.disassembler.render.command.CommandFactory;
import ru.zxspectrum.disassembler.render.command.Instruction;
import ru.zxspectrum.disassembler.render.command.Variable;
import ru.zxspectrum.disassembler.render.system.Org;
import ru.zxspectrum.disassembler.settings.DefaultSettings;
import ru.zxspectrum.disassembler.settings.DisassemblerSettings;
import ru.zxspectrum.disassembler.utils.FileUtils;
import ru.zxspectrum.disassembler.utils.ObjectUtils;
import ru.zxspectrum.disassembler.utils.SymbolUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Maxim Gorin
 * Date: 12/24/2023
 */
@Slf4j
public class Disassembler {
    protected static final String EXT = "asm";

    private Map<BigInteger, String> labelMap = new HashMap<>();

    private static final Tree<CommandFactory> TREE = new Tree<>();

    private DisassemblerSettings settings;

    private int errorCount;

    private static int addressSize;

    private List<File> successfullyDisassembled = new LinkedList<>();

    private Disassembler() {
    }

    public Disassembler(@NonNull DisassemblerSettings settings) {
        setSettings(settings);
    }

    protected void reset() {
        errorCount = 0;
        labelMap.clear();
        addressSize = 0;
        successfullyDisassembled.clear();
    }

    protected void setSettings(@NonNull DisassemblerSettings settings) {
        this.settings = settings;
        addressSize = Type.getSize(settings.getMaxAddress().longValue());
        if (addressSize <= 0) {
            throw new IllegalArgumentException("Bad address size");
        }
    }

    protected Collection<String> setCli(@NonNull final String[] args, @NonNull final Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cli = parser.parse(options, args);
            settings.load(cli);
            return cli.getArgList();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public void run(@NonNull final File... files) {
        Output.println(createWelcome());
        for (final File file : files) {
            try {
                runSingle(file);
                successfullyDisassembled.add(file);
            } catch (RenderException | InterruptedException | IOException e) {
                errorCount++;
                log.error(e.getMessage(), e);
            } catch (ExceptionGroup e) {
                errorCount += e.getExceptions().size();
                log.error(e.getMessage(), e);
            }
        }
        showTotalReport();
    }

    private void runSingle(final File file) throws IOException, InterruptedException, RenderException {
        final ByteCodeInputStream is = new ByteCodeInputStream(file, Files.readAllBytes(file.toPath())
                , settings.getByteOrder());
        final Canvas canvas = new Canvas();
        final DecoderExecutor executor = new DecoderExecutor();
        final Decoder decoder = new Decoder(executor, TREE, settings.getDefaultAddress(), is);
        decoder.setOutput(canvas);
        executor.execute(decoder);
        executor.await();
        executor.shutdown();
        final Collection<Throwable> errors = executor.getErrors();
        if (!errors.isEmpty()) {
            throw new ExceptionGroup(errors);
        }
        enrichSystem(decoder, canvas);
        enrichLabel(decoder, canvas);
        enrichComment(canvas);
        final File outputFile = FileUtils.createNewFileSameName(new File("."), file.getAbsoluteFile(), EXT);
        canvas.setFile(outputFile);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            canvas.flush(fos);
        }
        showReport(canvas);
    }

    private void enrichSystem(@NonNull Decoder decoder, @NonNull Canvas canvas) {
        canvas.setOrg(RowFactory.createRow(new Org(decoder.getFirstAddress())));
    }

    private void enrichLabel(Decoder decoder, @NonNull final Canvas canvas) {
        canvas.walkThrough(entry -> {
            final Row row = entry.getValue();
            if (row.getCommand() instanceof Instruction) {
                Instruction inst = (Instruction) row.getCommand();
                for (int i = 0; i < inst.getVariableCount(); i++) {
                    final ByteCodeUnit unit = inst.getUnits().getPattern(i);
                    final Variable var = inst.getVariable(i);
                    if (Type.getByPattern(unit.getValue()).getSize() == addressSize ||
                            unit.getValue().startsWith("e")) { //offset
                        final BigInteger address = var.getValue();
                        String labelName = labelMap.get(address);
                        if (labelName != null) {
                            var.setName(labelName);
                        } else {
                            final Row labelRow = canvas.get(address);
                            if (labelRow != null) {
                                labelName = ObjectUtils.generateLabelName(address, addressSize);
                                labelMap.put(address, labelName);
                                labelRow.setLabel(new Label(labelName));
                                var.setName(labelName);
                            } else {
                                if (ObjectUtils.isInRange(decoder.getFirstAddress(), decoder.getLastAddress(), address)) {
                                    log.info("Address {} is not disasmed", address);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void enrichComment(@NonNull final Canvas canvas) {

    }

    private void showReport(Canvas canvas) {
        Output.println(Messages.getMessage(Messages.DISASSEMBLED_LINES_IN), String.valueOf(canvas.getLineCount()),
                canvas.getFile().getName());
    }

    private void showTotalReport() {
        Output.println(Messages.getMessage(Messages.TOTAL_ERRORS), String.valueOf(errorCount));
        Output.println(Messages.getMessage(Messages.SUCCESSFULLY_DISASSEMBLED),
                String.valueOf(successfullyDisassembled.size()));
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
            Options options = getOptions();
            if (args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(settings.getCmdFilename() + " <file1>...<fileN>", options);
                return;
            }
            final Collection<String> fileNames = disassembler.setCli(args, options);
            if (fileNames.isEmpty()) {
                Output.println("No input files");
                return;
            }
            final List<File> files = new LinkedList<>();
            for (String fileName : fileNames) {
                files.add(new File(fileName));
            }
            disassembler.run(files.toArray(new File[files.size()]));
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    protected static DisassemblerSettings loadSettings() {
        final DisassemblerSettings settings = new DisassemblerSettings();
        settings.merge(new DefaultSettings());
        try {
            settings.load(Disassembler.class.getResourceAsStream("/settings.properties"));
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
        options.addOption("v", "visible", false, "Show or not address in decompiled file.");
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
}
