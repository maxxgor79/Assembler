package ru.assembler.core.compiler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.IOUtils;
import ru.assembler.core.compiler.command.nonparameterized.NonParametersCommandCompiler;
import ru.assembler.core.compiler.command.parameterized.ParameterizedCommandCompiler;
import ru.assembler.core.compiler.command.system.DbCommandCompiler;
import ru.assembler.core.compiler.command.system.DdwCommandCompiler;
import ru.assembler.core.compiler.command.system.DefCommandCompiler;
import ru.assembler.core.compiler.command.system.DwCommandCompiler;
import ru.assembler.core.compiler.command.system.EndCommandCompiler;
import ru.assembler.core.compiler.command.system.EquCommandCompiler;
import ru.assembler.core.compiler.command.system.IncludeCommandCompiler;
import ru.assembler.core.compiler.command.system.OrgCommandCompiler;
import ru.assembler.core.compiler.command.system.UdefCommandCompiler;
import ru.assembler.core.compiler.command.tree.CommandTree;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.compiler.option.OptionType;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.error.text.Output;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemAnalyzer;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.settings.Variables;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.syntax.SyntaxAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ru.assembler.core.util.RepeatableIteratorImpl;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class Compiler implements CompilerApi {

  private static final String TEMPLATE_NAME = "template";

  protected final NamespaceApi namespaceApi;

  protected final SettingsApi settingsApi;

  private int lineNumber;

  private int compiledLineCount;

  private final SyntaxAnalyzer syntaxAnalyzer;

  private final OutputStream os;

  private File file;

  private int sourceCount;

  private final CommandTree commandCompilerTree;

  private final Map<LexemSequence, CommandCompiler> commandCompilerMap = new HashMap<>();

  private final Map<OptionType, Option> optionsMap = new HashMap<>();

  private boolean stopped;

  private final Set<File> includedSet = new HashSet<>();

  public Compiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
      , @NonNull SyntaxAnalyzer syntaxAnalyzer, @NonNull OutputStream os) {
    this.namespaceApi = namespaceApi;
    this.settingsApi = settingsApi;
    this.syntaxAnalyzer = syntaxAnalyzer;
    this.os = os;
    this.commandCompilerTree = new CommandTree();
    initDefaultValues();
  }

  private void initDefaultValues() {
  }

  private boolean processCommand(LexemSequence lexemSequence) throws IOException {
    CommandCompiler compiler = commandCompilerMap.get(
        new LexemSequence(lexemSequence.first()));//embedded instruction
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
      throw new CompilerException(lexem.getFile(), lexem.getLineNumber(),
          MessageList.getMessage(MessageList
              .LABEL_IS_ALREADY_DEFINED), lexem.getValue());
    }
    namespaceApi.putLabel(lexem.getValue());
  }

  private static void putCommandCompiler(final CommandCompiler cc, final Map<LexemSequence
      , CommandCompiler> commandCompilerMap) {
    for (String name : cc.names()) {
      commandCompilerMap.put(new LexemSequence(name), cc);
    }
  }

  private void loadEmbeddedCommands(Map<LexemSequence, CommandCompiler> commandCompilerMap) {
    putCommandCompiler(new OrgCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
    putCommandCompiler(new DbCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
    putCommandCompiler(new DwCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
    putCommandCompiler(new DdwCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
    putCommandCompiler(new IncludeCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
    putCommandCompiler(new DefCommandCompiler(namespaceApi, this)
        , commandCompilerMap);
    putCommandCompiler(new UdefCommandCompiler(namespaceApi, this)
        , commandCompilerMap);
    putCommandCompiler(new EndCommandCompiler(namespaceApi, this)
        , commandCompilerMap);
    putCommandCompiler(new EquCommandCompiler(namespaceApi, settingsApi, this)
        , commandCompilerMap);
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
      is = Compiler.class.getResourceAsStream(path);
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
    Output.println(
        MessageList.getMessage(MessageList.COMPILING) + " " + getFile().getAbsolutePath());
    for (LexemSequence lexemSequence : syntaxAnalyzer) {
      Lexem lexem = lexemSequence.first();
      setLineNumber(lexem.getLineNumber());
      if (lexem.getType() == LexemType.LABEL) {
        processLabel(lexem);
      } else {
        if (!processCommand(lexemSequence)) {
          throw new CompilerException(lexemSequence.getFile(), lexemSequence.getLineNumber(),
              MessageList
                  .getMessage(MessageList.UNKNOWN_COMMAND), lexemSequence.getCaption());
        }
      }
      if (isStopped()) {
        break;
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

  @Override
  public boolean addCommand(@NonNull String name, @NonNull CommandCompiler commandCompiler) {
    final LexemSequence lexemSequence = new LexemSequence(name);
    if (commandCompilerMap.containsKey(lexemSequence)) {
      return false;
    }
    commandCompilerMap.put(lexemSequence, commandCompiler);
    return true;
  }

  @Override
  public void stop() {
    this.stopped = true;
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public boolean include(@NonNull String path) throws IOException {
    final File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException(file.getAbsolutePath());
    }
    if (includedSet.contains(file)) {
      return false;
    }
    includedSet.add(file);
    byte[] data;
    try (FileInputStream fis = new FileInputStream(file)) {
      data = IOUtils.toByteArray(fis);
    }
    final LexemAnalyzer lexemAnalyzer = new LexemAnalyzer(file, new ByteArrayInputStream(data)
        , settingsApi.getPlatformEncoding(), settingsApi.getSourceEncoding());
    lexemAnalyzer.setTrimEof(false);
    syntaxAnalyzer.append(new RepeatableIteratorImpl<>(lexemAnalyzer.iterator()));
    addCompiledSourceCount(1);
    return true;
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
