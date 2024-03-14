package ru.retro.assembler.editor.core.ui.compile;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.ConsoleWriter;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.util.CLIUtils;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.editor.core.util.UIUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class ExternalCompiling implements Compiling {
    private AppSettings settings;

    private MainWindow mainWindow;

    public ExternalCompiling(@NonNull final AppSettings settings, @NonNull final MainWindow mainWindow) {
        this.settings = settings;
        this.mainWindow = mainWindow;
    }

    public void compile(@NonNull final Source src, String... args) {
        final String asmDir = settings.getCompilerPath();
        final String outputDir = settings.getOutputDirectory();
        final File pathToAsm = new File(asmDir, CLIUtils.ASM_FILENAME);
        try {
            if (!pathToAsm.exists()) {
                throw new FileNotFoundException(pathToAsm.getAbsolutePath());
            }
            final java.util.List<String> argList = CLIUtils.toList(pathToAsm.getAbsolutePath(),
                    String.format(CLIUtils.ARG_LOCALE, UIUtils.toLocale(settings.getLanguage()).getLanguage())
                    , CLIUtils.ARG_OUTPUT
                    , outputDir
                    , src.getFile().getAbsolutePath()
                    , args);
            final Process p = new ProcessBuilder(argList).start();
            final ConsoleWriter consoleWriter = new ConsoleWriter(p.getInputStream(), mainWindow.getConsole().getArea());
            SwingUtilities.invokeLater(consoleWriter);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow, String.format(Messages.get(Messages.FILE_NOT_FOUND)
                            , pathToAsm.getAbsolutePath()), Messages.get(Messages.ERROR), JOptionPane.ERROR_MESSAGE
                    , ResourceUtils.getErrorIcon());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow,
                    String.format(Messages.get(Messages.IO_ERROR), src.getFile()
                            .getAbsolutePath()), Messages.get(Messages.ERROR), JOptionPane.ERROR_MESSAGE
                    , ResourceUtils.getErrorIcon());
        }
    }
}
