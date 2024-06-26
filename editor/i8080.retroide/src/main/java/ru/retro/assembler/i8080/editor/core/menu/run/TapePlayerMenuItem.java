package ru.retro.assembler.i8080.editor.core.menu.run;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.ui.menu.AbstractMenuItem;
import ru.retro.assembler.editor.core.ui.player.Player;
import ru.retro.assembler.editor.core.ui.progress.SimpleWorker;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileWavMenuItem;
import ru.retro.assembler.i8080.editor.utils.ResourceUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

@Slf4j
public class TapePlayerMenuItem extends AbstractMenuItem {
    protected static final String EXTENSION = "wav";

    private Player player;

    private final MainWindow mainWindow;

    private final AppSettings settings;

    public TapePlayerMenuItem(
            @NonNull Controller controller) {
        super(controller, I8080Messages.getInstance().get(I8080Messages.TAPE_PLAYER), (char) 0, KeyStroke
                .getKeyStroke(KeyEvent.VK_F10, InputEvent.ALT_DOWN_MASK | InputEvent
                        .SHIFT_DOWN_MASK), "/icon16x16/play.png");
        this.mainWindow = controller.getMainWindow();
        this.settings = controller.getSettings();
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean hasSeparator() {
        return true;
    }

    @Override
    public void onAction(ActionEvent actionEvent) {
        log.info("Action play");
        final SimpleWorker<Void> worker = new SimpleWorker<>(controller.getMainWindow()) {

            @Override
            protected Void perform() throws Exception {
                final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
                if (selectedSource == null) {
                    return null;
                }
                play(actionEvent, selectedSource.getFile());
                return null;
            }
        };
        try {
            worker.execute();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void play(ActionEvent actionEvent, @NonNull final File file) {
        String name = file.getName();
        name = ResourceUtils.cutExtension(name);
        final File wavFile = new File(settings.getOutputDirectory(), name + "." + EXTENSION);
        final ru.retro.assembler.editor.core.ui.player.Player player = getPlayer();
        player.setFile(wavFile);
        player.setLocationRelativeTo(mainWindow);
        SwingUtilities.invokeLater(() -> player.showModal());
    }

    @Override
    public boolean isEnabled() {
        return mainWindow.getSourceTabbedPane().getTabCount() != 0;
    }

    private Player getPlayer() {
        if (player == null) {
            player = new Player(controller.getMainWindow());
        }
        return player;
    }
}