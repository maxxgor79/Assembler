package ru.zxspectrum.assembler.compiler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.command.CommandTable;
import ru.zxspectrum.assembler.compiler.command.noparameterized.NoParameterizedSingleCommandGroupCompiler;
import ru.zxspectrum.assembler.compiler.command.noparameterized.NoParametersCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.parameterized.ParameterizedCommandCompiler;
import ru.zxspectrum.assembler.compiler.command.parameterized.ParameterizedCommandGroupCompiler;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
@Slf4j
public class AssemblerCommandLoader extends CommandLoader<CommandTable> {
    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

    public AssemblerCommandLoader(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.compilerApi = compilerApi;
    }

    private static boolean isNumber(LexemType type) {
        return switch (type) {
            case HEXADECIMAL, DECIMAL, OCTAL, BINARY -> true;
            default -> false;
        };
    }

    private LexemSequence extractKey(LexemSequence lexemSequence) {
        List<Lexem> lexemList = new LinkedList<>();
        for (Lexem lexem : lexemSequence.get()) {
            if (lexem.getType() == LexemType.VARIABLE || isNumber(lexem.getType())) {
                break;
            }
            lexemList.add(lexem);
        }
        if (lexemList.isEmpty()) {
            return null;
        }
        return new LexemSequence(lexemList);
    }

    @Override
    protected void parse(CommandTable commandTable, int lineNumber, String code, String command) {
        LexemSequence commandPattern = new LexemSequence(command);
        LexemSequence key = extractKey(commandPattern);
        if (key != null) {
            if (commandPattern.equals(key)) {
                commandTable.put(key, new NoParameterizedSingleCommandGroupCompiler(new NoParametersCommandCompiler(compilerApi
                        , code, commandPattern)));
            } else {
                CommandGroupCompiler group = commandTable.get(key);
                if (group == null) {
                    group = new ParameterizedCommandGroupCompiler();
                    commandTable.put(key, group);
                }
                group.add(new ParameterizedCommandCompiler(namespaceApi, settingsApi, compilerApi
                        , code, commandPattern));
            }
        }
    }

    @Override
    public CommandTable load(@NonNull InputStream is, Charset encoding) throws IOException {
        CommandTable commandTable = new CommandTable();
        load(commandTable, is, encoding);
        return commandTable;
    }
}
