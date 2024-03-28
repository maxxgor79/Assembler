package ru.zxspectrum.disassembler.settings;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import ru.zxspectrum.disassembler.decode.DecoderStrategy;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.utils.ConverterUtils;
import ru.zxspectrum.disassembler.utils.NumberStyle;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DisassemblerSettings extends BaseSettings {
    public void load(@NonNull CommandLine cli) {
        // parse the command line arguments
        if (cli.hasOption("a")) {
            setDefaultAddress(ConverterUtils.numberToInteger(cli.getOptionValue("a")));
        }
        if (cli.hasOption("min")) {
            setMinAddress(new BigInteger(cli.getOptionValue("min")));
        }
        if (cli.hasOption("max")) {
            setMaxAddress(new BigInteger(cli.getOptionValue("max")));
        }
        if (cli.hasOption("o")) {
            setOutputDirectory(cli.getOptionValue("o"));
        }
        if (cli.hasOption("b")) {
            setByteOrder("big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                    ByteOrder.LittleEndian);
        }
        if (cli.hasOption("v")) {
            setAddressVisible(true);
        }
        if (cli.hasOption("stdout")) {
            setStdout(true);
        }
        if (cli.hasOption("r")) {
            final int radix = ConverterUtils.toRadixIndex(cli.getOptionValue("r"));
            if (radix != -1) {
                setRadix(radix);
            }
        }
        if (cli.hasOption("lc")) {
            final boolean b = ConverterUtils.toUpperCase(cli.getOptionValue("lc"));
            setUpperCase(b);
        }

        if (cli.hasOption("ns")) {
            final NumberStyle ns = ConverterUtils.toNumberStyle(cli.getOptionValue("ns"));
            setNumberStyle(ns);
        }
        if (cli.hasOption("strategy")) {
            final DecoderStrategy strategy = ConverterUtils.toStrategy(cli.getOptionValue("strategy"));
            setStrategy(strategy);
        }
    }

    @Override
    public void merge(@NonNull Settings settings) {
        setAddressVisible(settings.isAddressVisible());
        setAddressDimension(settings.getAddressDimension());
        setRadix(settings.getRadix());
        setUpperCase(settings.isUpperCase());
        if (getDefaultAddress() == null) {
            setDefaultAddress(settings.getDefaultAddress());
        }
        if (settings.getByteOrder() != null) {
            setByteOrder(settings.getByteOrder());
        }
        if (settings.getDestEncoding() != null) {
            setDestEncoding(settings.getDestEncoding());
        }
        if (settings.getCommentsTemplate() != null) {
            setCommentsTemplate(settings.getCommentsTemplate());
        }
        if (settings.getMajorVersion() != null) {
            setMajorVersion(settings.getMajorVersion());
        }
        if (settings.getMinorVersion() != null) {
            setMinorVersion(settings.getMinorVersion());
        }
        if (settings.getMaxAddress() != null) {
            setMaxAddress(settings.getMaxAddress());
        }
        if (settings.getMinAddress() != null) {
            setMinAddress(settings.getMinAddress());
        }
        if (settings.getCmdFilename() != null) {
            setCmdFilename(settings.getCmdFilename());
        }
        if (settings.getTemplates() != null) {
            setTemplates(settings.getTemplates());
        }
        if (settings.getNumberStyle() != null) {
            setNumberStyle(settings.getNumberStyle());
        }
        if (settings.getStrategy() != null) {
            setStrategy(settings.getStrategy());
        }
    }
}
