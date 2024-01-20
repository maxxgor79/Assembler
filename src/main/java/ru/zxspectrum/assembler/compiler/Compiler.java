package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import ru.zxspectrum.assembler.Z80Assembler;
import ru.zxspectrum.assembler.compiler.command.nonparameterized.NonParametersCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.parameterized.ParameterizedCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DbCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DdwCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DefCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.DwCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.IncludeCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.OrgCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.TapCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.UdefCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.system.WavCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.tree.CommandTree;
import ru.zxspectrum.assembler.compiler.option.Option;
import ru.zxspectrum.assembler.compiler.option.OptionType;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.settings.Variables;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.syntax.SyntaxAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class Compiler implements CompilerApi {
    private static final String TEMPLATE_NAME = "template";

    private final NamespaceApi namespaceApi;

    private final SettingsApi settingsApi;

    private int lineNumber;

    private int compiledLineCount;

    private final SyntaxAnalyzer syntaxAnalyzer;

    private final OutputStream os;

    private File file;

    private int sourceCount;

    private final CommandTree commandCompilerTree = new CommandTree();

    private final Map<LexemSequence, CommandCompiler> commandCompilerMap = new HashMap<>();

    private final Map<OptionType, Option> optionsMap = new HashMap<>();

    public Compiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.syntaxAnalyzer = syntaxAnalyzer;
        this.os = os;
        initDefaultValues();
    }

    private void initDefaultValues() {
        namespaceApi.setAddress(Variables.getBigInteger(Variables.DEFAULT_ADDRESS
                , BigInteger.valueOf(0x8000)));
    }

    private boolean processCommand(LexemSequence lexemSequence) throws IOException {
        CommandCompiler compiler = commandCompilerMap.get(new LexemSequence(lexemSequence.first()));//embedded instruction
        if (compiler == null) {
            compiler = commandCompilerTree.find(lexemSequence);//try to find by pattern from lexemSequence
            if (compiler == null) {
                return false;
            }
        }
        final byte[] commandData = compiler.compile(lexemSequence);
        if (commandData == null) {
            return false;
        }
        namespaceApi.incCurrentCodeOffset(BigInteger.valueOf(commandData.length));
        os.write(commandData);
        return true;
    }

    private void processLabel(Lexem lexem) {
        if (namespaceApi.containsLabel(lexem.getValue())) {
            throw new CompilerException(getFile(), lexem.getLineNumber(), MessageList.getMessage(MessageList
                    .LABEL_IS_ALREADY_DEFINED), lexem.getValue());
        }
        namespaceApi.putLabel(lexem.getValue());
    }

    private void loadEmbeddedCommands(Map<LexemSequence, CommandCompiler> commandCompilerMap) {
        commandCompilerMap.put(new LexemSequence(OrgCommandCompiler.NAME), new OrgCommandCompiler(namespaceApi
                , settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DbCommandCompiler.NAME), new DbCommandCompiler(DbCommandCompiler.NAME
                , namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DbCommandCompiler.ALT_NAME), new DbCommandCompiler(DbCommandCompiler
                .ALT_NAME, namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DwCommandCompiler.NAME), new DwCommandCompiler(DwCommandCompiler.NAME
                , namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DwCommandCompiler.ALT_NAME), new DwCommandCompiler(DwCommandCompiler
                .ALT_NAME, namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DdwCommandCompiler.NAME), new DdwCommandCompiler(DdwCommandCompiler.NAME
                , namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DdwCommandCompiler.ALT_NAME), new DdwCommandCompiler(DdwCommandCompiler
                .ALT_NAME, namespaceApi, settingsApi, this));
        commandCompilerMap.put(new LexemSequence(IncludeCommandCompiler.NAME), new IncludeCommandCompiler(namespaceApi
                , settingsApi, this));
        commandCompilerMap.put(new LexemSequence(DefCommandCompiler.NAME), new DefCommandCompiler(DefCommandCompiler
                .NAME, namespaceApi, this));
        commandCompilerMap.put(new LexemSequence(DefCommandCompiler.ALT_NAME), new DefCommandCompiler(DefCommandCompiler
                .ALT_NAME, namespaceApi, this));
        commandCompilerMap.put(new LexemSequence(UdefCommandCompiler.NAME), new UdefCommandCompiler(UdefCommandCompiler
                .NAME, namespaceApi, this));
        commandCompilerMap.put(new LexemSequence(UdefCommandCompiler.ALT_NAME), new UdefCommandCompiler(UdefCommandCompiler
                .ALT_NAME, namespaceApi, this));
        commandCompilerMap.put(new LexemSequence(WavCommandCompiler.NAME), new WavCommandCompiler(this));
        commandCompilerMap.put(new LexemSequence(TapCommandCompiler.NAME), new TapCommandCompiler(this));

    }

    private void loadCustomCommands(CommandTree commandCompilerTree) throws IOException {
        List<String> templatePath = new LinkedList<>();
        int i = 0;
        while (true) {
            String path = Variables.getString(TEMPLATE_NAME + i, null);
            if (path == null) {
                break;
            }
            templatePath.add(path);
            i++;
        }
        AssemblerCommandLoader assemblerCommandLoader = new AssemblerCommandLoader(settingsApi);
        InputStream is;
        for (String path : templatePath) {
            is = Z80Assembler.class.getResourceAsStream(path);
            if (is != null) {
                MultiValuedMap<String, LexemSequence> map = assemblerCommandLoader.load(is);
                for (Map.Entry<String, LexemSequence> entry : map.entries()) {
                    if (entry.getValue().hasVariables()) {
                        commandCompilerTree.add(entry.getValue(), new ParameterizedCommandCompiler(namespaceApi
                                , settingsApi, this, entry.getKey(), entry.getValue()));
                    } else {
                        commandCompilerTree.add(entry.getValue(), new NonParametersCommandCompiler(this
                                , entry.getKey(), entry.getValue()));
                    }
                }

            }
        }
    }

    private void loadCommandTables() throws IOException {
        loadEmbeddedCommands(commandCompilerMap);
        loadCustomCommands(commandCompilerTree);
        if (commandCompilerTree.isEmpty()) {
            throw new CompilerException(MessageList.getMessage(MessageList.COMMAND_DATA_IS_NOT_LOADED));
        }
    }

    @Override
    public void compile() throws IOException {
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

    @Override
    public boolean hasOption(@NonNull OptionType type) {
        return optionsMap.containsKey(type);
    }

    @Override
    public Option getOption(@NonNull OptionType type) {
        return optionsMap.get(type);
    }

    @Override
    public boolean addOption(@NonNull Option option) {
        return optionsMap.put(option.getType(), option) != null;
    }

    public void setFile(@NonNull File file) {
        this.file = file;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    protected void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
