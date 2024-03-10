package ru.retro.assembler.editor.core.ui.compile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.EmbeddedStarter;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.io.TextAreaOutputStream;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.util.CLIUtils;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.PrintStream;
import java.util.List;

@Slf4j
public class EmbeddedCompiling implements Compiling {
    private AppSettings settings;

    private MainWindow mainWindow;

    public EmbeddedCompiling(@NonNull final AppSettings settings, @NonNull final MainWindow mainWindow) {
        this.settings = settings;
        this.mainWindow = mainWindow;
    }

    @Override
    public void compile(@NonNull Source src, String... args) {
        final String outputDir = settings.getOutputDirectory();
        final List<String> argList = CLIUtils.toList(CLIUtils.ARG_OUTPUT, outputDir, src.getFile()
                .getAbsolutePath(), args);
        System.setOut(new PrintStream(new TextAreaOutputStream(mainWindow.getConsole().getArea())));
        EmbeddedStarter starter = new EmbeddedStarter(argList, mainWindow);
        SwingUtilities.invokeLater(starter);
    }
}
