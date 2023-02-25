package ru.zxspectrum.assembler;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.CompilerFactory;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Encoding;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
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
 *
 */

public class Assembler implements NamespaceApi, SettingsApi {
    private static final Logger logger = LogManager.getLogger(Assembler.class.getName());

    private BigInteger address = BigInteger.ZERO;

    private BigInteger currentCodeOffset = BigInteger.ZERO;

    private Encoding sourceEncoding = Encoding.UTF_8;

    private Encoding platformEncoding = Encoding.ASCII;

    private ByteOrder byteOrder = ByteOrder.LittleEndian;

    private BigInteger minAddress = BigInteger.ZERO;

    private BigInteger maxAddress = BigInteger.valueOf(Integer.MAX_VALUE);

    private final Map<String, LabelInfo> labelMap = new HashMap<>();

    private final Map<String, BigInteger> variableMap = new HashMap<>();

    private final List<PostCommandCompiler> postCommandCompilerList = new LinkedList<>();

    private final Set<File> compiledFileSet = new HashSet<>();

    private static String majorVersion = "1";

    private static String minorVersion = "0";

    public Assembler() {
        loadSettings();
    }

    private void loadSettings() {
        try {
            Variables.load(Assembler.class.getResourceAsStream("/settings.properties"));
            platformEncoding = Encoding.valueByName(Variables.getString(Variables.PLATFORM_ENCODING, Encoding.ASCII.getName()));
            sourceEncoding = Encoding.valueByName(Variables.getString(Variables.SOURCE_ENCODING, Encoding.UTF_8.getName()));
            String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
            if ("big-endian".equals(value)) {
                byteOrder = ByteOrder.BigEndian;
            } else {
                byteOrder = ByteOrder.LittleEndian;
            }
            minAddress = Variables.getBigInteger(Variables.MIN_ADDRESS, BigInteger.ZERO);
            maxAddress = Variables.getBigInteger(Variables.MAX_ADDRESS, BigInteger.valueOf(0xFFFF));
            majorVersion = Variables.getString(Variables.MAJOR_VERSION, "1");
            minorVersion = Variables.getString(Variables.MINOR_VERSION, "0");
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    private static File removeExtension(File file) {
        String fileName = file.getName();
        int i = fileName.indexOf('.');
        if (i != -1) {
            fileName = fileName.substring(0, i);
        }
        return new File(file.getParentFile(), fileName);
    }

    public void run(File... files) throws IOException {
        FileOutputStream fos = null;
        try {

            for (File file : files) {
                File outpuitFile = removeExtension(file);
                fos = new FileOutputStream(outpuitFile);
                CompilerApi compilerApi = runSingle(file, fos);
                fos.close();
                fos = null;
                postCompile(outpuitFile);
                Output.formatPrintln("%d %s", Output.getWarningCount(), MessageList.getMessage(MessageList.N_WARNINGS));
                Output.formatPrintln("%s %s %d %s, %d %s",MessageList.getMessage(MessageList.COMPILED1),
                        MessageList.getMessage(MessageList.SUCCESSFULLY) , compilerApi.getCompiledLineCount(), MessageList
                                .getMessage(MessageList.LINES), compilerApi.getCompiledSourceCount(), MessageList
                                .getMessage(MessageList.SOURCES));
            }
        } catch (Exception e) {
            Output.println(e.getMessage());
            logger.log(Level.DEBUG, e.getMessage());
        } finally {
            if (fos == null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    logger.log(Level.DEBUG, e.getMessage());
                }
            }
        }
    }

    protected CompilerApi runSingle(File file, OutputStream os) throws IOException {
        CompilerApi compilerApi = CompilerFactory.create(this, this, file, os);
        compilerApi.compile();
        return compilerApi;
    }

    private static String createHeader() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(MessageList.getMessage(MessageList.PROGRAM_WELCOME), majorVersion
                , minorVersion);
        String writtenBy = MessageList.getMessage(MessageList.WRITTEN_BY);
        String lineExternal = SymbolUtils.fillChar('*', 80);
        sb.append(lineExternal).append("\n");
        String lineInternal = (new StringBuilder().append('*').append(SymbolUtils.fillChar(' ', 78))
                .append('*')).toString();
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
                , programWelcome)).append("\n");
        sb.append(SymbolUtils.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2
                , writtenBy)).append("\n");
        sb.append(lineExternal).append("\n");
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Output.println("Usage: assembler <file1.asm> <file2.asm> ... <fileN.asm>");
            return;
        }
        Assembler assembler = new Assembler();
        List<File> fileList = new LinkedList<>();
        for (String arg : args) {
            fileList.add(new File(arg));
        }
        Output.print(createHeader());
        assembler.run(fileList.toArray(new File[fileList.size()]));
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