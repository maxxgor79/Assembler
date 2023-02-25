package ru.zxspectrum.assembler.compiler;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.assembler.Assembler;
import ru.zxspectrum.assembler.NamespaceApi;
import ru.zxspectrum.assembler.compiler.command.CommandTable;
import ru.zxspectrum.assembler.compiler.command.noparameterized.NoParameterizedSingleCommandGroupCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DbCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DefCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.IncludeCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.OrgCommandCompiler;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.syntax.SyntaxAnalyzer;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
public class Compiler implements CompilerApi {
    private static final Logger logger = LogManager.getLogger(Compiler.class.getName());
    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private int lineNumber;

    private int compiledLineCount;

    private SyntaxAnalyzer syntaxAnalyzer;

    private InputStream is;

    private OutputStream os;

    private File file;

    private int sourceCount;

    protected CommandTable commandCompilerTable;

    public Compiler(NamespaceApi namespaceApi, SettingsApi settingsApi, SyntaxAnalyzer syntaxAnalyzer
            , FileInputStream is, OutputStream os) {
        if (namespaceApi == null) {
            throw new NullPointerException("namespaceApi");
        }
        this.namespaceApi = namespaceApi;
        if (settingsApi == null) {
            throw new NullPointerException("settingsApi");
        }
        this.settingsApi = settingsApi;
        if (syntaxAnalyzer == null) {
            throw new NullPointerException("syntaxAnalyzer");
        }
        this.syntaxAnalyzer = syntaxAnalyzer;
        if (os == null) {
            throw new NullPointerException("os");
        }
        this.os = os;
        if (is == null) {
            throw new NullPointerException("is");
        }
        this.is = is;
        initDefaultValues();
    }

    private void initDefaultValues() {
        namespaceApi.setAddress(Variables.getBigInteger(Variables.DEFAULT_ADDRESS
                , BigInteger.valueOf(0x8000)));
    }

    private boolean processCommand(LexemSequence lexemSequence) throws IOException {
        CommandCompiler compiler = commandCompilerTable.get(lexemSequence);
        if (compiler != null) {
            byte[] commandData = compiler.compile(lexemSequence);
            namespaceApi.incCurrentCodeOffset(BigInteger.valueOf(commandData.length));
            os.write(commandData);
            return true;
        } else {
            List<Lexem> lexemList = new LinkedList<>();
            for (Lexem lexem : lexemSequence.get()) {
                lexemList.add(lexem);
                compiler = commandCompilerTable.get(new LexemSequence(lexemList));
                if (compiler != null) {
                    byte[] commandData = compiler.compile(lexemSequence);
                    if (commandData != null) {
                        namespaceApi.incCurrentCodeOffset(BigInteger.valueOf(commandData.length));
                        os.write(commandData);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processLabel(Lexem lexem) throws IOException {
        if (namespaceApi.containsLabel(lexem.getValue())) {
            throw new CompilerException(getFile(), lexem.getLineNumber(), MessageList.getMessage(MessageList
                    .LABEL_IS_ALREADY_DEFINED), lexem.getValue());
        }
        namespaceApi.putLabel(lexem.getValue());
    }

    private void loadSystemCommands() {
        commandCompilerTable.put(new LexemSequence(OrgCommandCompiler.NAME), new NoParameterizedSingleCommandGroupCompiler
                (new OrgCommandCompiler(namespaceApi, settingsApi, this)));
        commandCompilerTable.put(new LexemSequence(DbCommandCompiler.NAME), new NoParameterizedSingleCommandGroupCompiler
                (new DbCommandCompiler(DbCommandCompiler.NAME, this, namespaceApi)));
        commandCompilerTable.put(new LexemSequence(DbCommandCompiler.ALT_NAME), new NoParameterizedSingleCommandGroupCompiler
                (new DbCommandCompiler(DbCommandCompiler.ALT_NAME, this, namespaceApi)));
        commandCompilerTable.put(new LexemSequence(IncludeCommandCompiler.NAME), new NoParameterizedSingleCommandGroupCompiler
                (new IncludeCommandCompiler(namespaceApi, settingsApi, this)));
        commandCompilerTable.put(new LexemSequence(DefCommandCompiler.NAME), new NoParameterizedSingleCommandGroupCompiler
                (new DefCommandCompiler(DefCommandCompiler.NAME, namespaceApi, this)));
        commandCompilerTable.put(new LexemSequence(DefCommandCompiler.ALT_NAME), new NoParameterizedSingleCommandGroupCompiler
                (new DefCommandCompiler(DefCommandCompiler.ALT_NAME, namespaceApi, this)));
    }

    private void loadCustomCommands() throws IOException {
        List<String> templatePath = new LinkedList<>();
        int i = 0;
        while (true) {
            String path = Variables.getString("template" + i, null);
            if (path == null) {
                break;
            }
            templatePath.add(path);
            i++;
        }
        AssemblerCommandLoader assemblerCommandLoader = new AssemblerCommandLoader(namespaceApi, settingsApi
                , this);
        InputStream is;
        for (String path : templatePath) {
            is = Assembler.class.getResourceAsStream(path);
            if (is != null) {
                commandCompilerTable.putAll(assemblerCommandLoader.load(is));
            }
        }
    }

    private void loadCommandTables() throws IOException {
        if (commandCompilerTable == null) {
            commandCompilerTable = new CommandTable();
            loadSystemCommands();
            loadCustomCommands();
        }
    }

    @Override
    public void compile() throws IOException {
        try {
            loadCommandTables();
            Output.println(MessageList.getMessage(MessageList.COMPILING) + " " + getFile().getAbsolutePath());
            for (LexemSequence lexemSequence : syntaxAnalyzer) {
                Lexem lexem = lexemSequence.first();
                setLineNumber(lexem.getLineNumber());
                if (lexem.getType() == LexemType.LABEL) {
                    processLabel(lexem);
                } else {
                    if (!processCommand(lexemSequence)) {
                        throw new CompilerException(getFile(), lexemSequence.getLineNumber(), MessageList
                                .getMessage(MessageList.UNKNOWN_COMMAND), lexemSequence.getCaption());
                    }
                }
            }
            sourceCount++;
            compiledLineCount += syntaxAnalyzer.getLineCount();
        } finally {
            safeClose(is);
        }
    }

    @Override
    public int getCompiledLineCount() {
        return compiledLineCount;
    }

    @Override
    public int addCompiledLineCount(int lineCount) {
        if (lineCount < 0) {
            throw new IllegalArgumentException("lineCount is negative");
        }
        return compiledLineCount += lineCount;
    }

    @Override
    public int getCompiledSourceCount() {
        return sourceCount;
    }

    @Override
    public int addCompiledSourceCount(int sourceCount) {
        if (sourceCount < 0) {
            throw new IllegalArgumentException("sourceCount is negative");
        }
        return this.sourceCount += sourceCount;
    }

    @Override
    public String getFileName() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getName();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public OutputStream getOutputStream() {
        return os;
    }

    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file");
        }
        this.file = file;
    }

    protected static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.log(Level.DEBUG, e.getMessage());
            }
        }
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    protected void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
