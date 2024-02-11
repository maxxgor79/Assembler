package ru.zxspectrum;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import ru.zxspectrum.converter.core.Converter;
import ru.zxspectrum.converter.core.Format;
import ru.zxspectrum.converter.i18n.ConvMessages;
import ru.zxspectrum.converter.settings.ConvSettings;
import ru.zxspectrum.converter.text.Formatter;
import ru.zxspectrum.error.ConverterException;
import ru.zxspectrum.error.UnsupportedFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class ZXConv {
    protected static final String SETTINGS_NAME = "settings.properties";

    private static ConvSettings settings;

    protected static ZXConv instance;

    protected File inputFile;

    protected File outputFile;

    protected Format inputFormat;

    protected Format outputFormat;

    private static ConvSettings loadSettings() {
        ConvSettings settings = new ConvSettings();
        try {
            settings.load(ZXConv.class.getResourceAsStream("/" + SETTINGS_NAME));
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        return settings;
    }

    protected void convert() throws ConverterException, IOException {
        switch (inputFormat) {
            case Tzx:
                switch (outputFormat) {
                    case Tzx -> Converter.copy(inputFile, outputFile);
                    case Tap -> Converter.tzx2Tap(inputFile, inputFile);
                    case Wav -> Converter.tzx2wav(inputFile, outputFile);
                    case Raw -> Converter.tzx2Raw(inputFile, outputFile);
                }
                break;
            case Tap:
                switch (outputFormat) {
                    case Tzx -> Converter.tap2tzx(inputFile, outputFile);
                    case Tap -> Converter.copy(inputFile, outputFile);
                    case Wav -> Converter.tap2wav(inputFile, outputFile);
                    case Raw -> Converter.tap2Raw(inputFile, outputFile);
                }
                break;
            case Wav:
                switch (outputFormat) {
                    case Wav -> Converter.copy(inputFile, outputFile);
                    case Tzx -> Converter.wav2tzx(inputFile, outputFile);
                    case Tap -> Converter.wav2tap(inputFile, outputFile);
                    case Raw -> Converter.wav2Raw(inputFile, outputFile);
                }
                break;
            case Raw:
                switch (outputFormat) {
                    case Raw -> Converter.copy(inputFile, outputFile);
                    case Wav -> Converter.raw2wav(inputFile, outputFile);
                    case Tap -> Converter.raw2tap(inputFile, outputFile);
                    case Tzx -> Converter.raw2tzx(inputFile, outputFile);
                }
                break;
            default:
                Converter.unsupported(inputFile, outputFile);
                break;
        }
    }

    private static void outputInfo() {
        System.out.println(ConvMessages.getMessage(ConvMessages.DESCRIPTION));
        System.out.println(ConvMessages.getMessage(ConvMessages.WRITTEN_BY));
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        settings = loadSettings();
        final Options options = getOptions();
        outputInfo();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(settings.getAppName() + " " + ConvMessages.getMessage(ConvMessages
                    .FILE_ENUM), options);
            return;
        }
        instance = new ZXConv();
        try {
            instance.setCli(parseCli(args, options));
        } catch (ParseException | FileNotFoundException | UnsupportedFormatException e) {
            log.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            return;
        }
        try {
            instance.convert();
        } catch (IOException | ConverterException e) {
            log.error(e.getMessage(), e);
            return;
        }
    }

    protected static CommandLine parseCli(String[] args, Options options) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        // parse the command line arguments
        return parser.parse(options, args);
    }

    protected static Options getOptions() {
        final Options options = new Options();
        options.addOption("in", "input-file", true, ConvMessages.getMessage(ConvMessages.INPUT_FILE));
        options.addOption("out", "output-file", true, ConvMessages.getMessage(ConvMessages.OUTPUT_FILE));
        options.addOption("if", "input-format", true, ConvMessages.getMessage(ConvMessages.INPUT_FORMAT));
        options.addOption("of", "output-format", true, ConvMessages.getMessage(ConvMessages.OUTPUT_FORMAT));
        return options;
    }

    protected void setCli(@NonNull CommandLine cli) throws FileNotFoundException, UnsupportedFormatException {
        if (cli.hasOption("in")) {
            inputFile = new File(cli.getOptionValue("in"));
        }
        if (inputFile == null) {
            throw new FileNotFoundException(ConvMessages.getMessage(ConvMessages.INPUT_FILE_REQUIRED));
        }
        if (cli.hasOption("out")) {
            outputFile = new File(cli.getOptionValue("out"));
        }
        String arg;
        if (cli.hasOption("if")) {
            arg = cli.getOptionValue("if");
        } else {
            arg = FilenameUtils.getExtension(inputFile.getName());
        }
        inputFormat = Format.getByCode(arg);
        if (inputFormat == null) {
            throw new UnsupportedFormatException(Formatter.format(ConvMessages.getMessage(ConvMessages
                    .UNKNOWN_INPUT_FORMAT), arg));
        }
        if (cli.hasOption("of")) {
            arg = cli.getOptionValue("of");
        } else {
            if (outputFile != null) {
                arg = FilenameUtils.getExtension(outputFile.getName());
            } else {
                arg = null;
            }
        }
        outputFormat = Format.getByCode(arg);
        if (outputFormat == null) {
            throw new UnsupportedFormatException(Formatter.format(ConvMessages.getMessage(ConvMessages
                    .UNKNOWN_OUTPUT_FORMAT), arg));
        }
        if (outputFile == null) {
            outputFile = new File (FilenameUtils.removeExtension(inputFile.getName()) + "."
                    + outputFormat.toString());
        }
    }
}
