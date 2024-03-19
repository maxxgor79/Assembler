package ru.retro.assembler.editor.core.ui.preferences;

import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.Store;
import ru.retro.assembler.editor.core.io.StoreException;
import ru.retro.assembler.editor.core.io.StoreFactory;
import ru.retro.assembler.editor.core.io.StoreType;
import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;

import javax.swing.*;
import java.awt.*;

import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.util.ResourceUtils;

/**
 * @Author: Maxim Gorin Date: 25.02.2024
 */
@Slf4j
public class PreferencesDialog extends JDialog implements ModalDialog {

  private static final int C_PATH = 0;

  private static final int C_OUTPUT = 1;

  private static final int ENCODING = 2;

  private static final int EDITOR_FONT = 3;

  private static final int EDITOR_FONT_SIZE = 4;

  private static final int EDITOR_BK_COLOR = 5;

  private static final int CONSOLE_FONT = 6;

  private static final int CONSOLE_FONT_SIZE = 7;

  private static final int CONSOLE_BK_COLOR = 8;

  private static final int CONSOLE_FONT_COLOR = 9;

  private static final int LANGUAGE = 10;

  private static final int EMBEDDED = 11;

  public static int OPTION_OK = 1;

  public static int OPTION_CANCEL = 0;

  private int result;

  @Getter
  private ButtonPanel buttonPanel;

  @Getter
  private PreferencesTabbedPane preferencesTabbedPane;

  private LocalizedOpenChooser openFileChooser;

  private final Store store = StoreFactory.getInstance(StoreType.MEMORY);

  public PreferencesDialog(Frame owner) {
    super(owner);
    initComponents();
  }

  private void initComponents() {
    Image image = null;
    try {
      image = ResourceUtils.loadImage("/icon16x16/chip.png");
      setIconImage(image);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    setTitle(Messages.getInstance().get(Messages.PREFERENCES));
    setModal(true);
    setLayout(new BorderLayout());
    buttonPanel = new ButtonPanel();
    add(buttonPanel, BorderLayout.SOUTH);
    preferencesTabbedPane = new PreferencesTabbedPane();
    add(preferencesTabbedPane, BorderLayout.CENTER);
    openFileChooser = new LocalizedOpenChooser();
    openFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    openFileChooser.setDialogTitle(Messages.getInstance().get(Messages.CHOOSE_DIRECTORY));
    pack();
    initListeners();
  }

  private void initListeners() {
    buttonPanel.getBtnOk().addActionListener(l -> {
      result = OPTION_OK;
      dispose();
    });
    buttonPanel.getBtnCancel().addActionListener(l -> {
      dispose();
    });
    preferencesTabbedPane.getCompilerPanel().getBtnCompilerPath().addActionListener(l -> {
      if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
        preferencesTabbedPane.getCompilerPanel().getCompilerPathField().setText(openFileChooser
            .getSelectedFile().getAbsolutePath());
      }
    });
    preferencesTabbedPane.getCompilerPanel().getBtnOutputDirectory().addActionListener(l -> {
      if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
        preferencesTabbedPane.getCompilerPanel().getOutputPathField().setText(openFileChooser
            .getSelectedFile().getAbsolutePath());
      }
    });
  }

  protected void store() {
    store.putString(C_PATH,
        preferencesTabbedPane.getCompilerPanel().getCompilerPathField().getText());
    store.putString(C_OUTPUT,
        preferencesTabbedPane.getCompilerPanel().getOutputPathField().getText());
    store.putObject(ENCODING,
        preferencesTabbedPane.getMiscellaneousPanel().getCharsetPanel().getCbEncoding()
            .getSelectedItem());
    store.putString(EDITOR_FONT,
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel()
            .getFontPanel().getSelectedFontName());
    store.putInt(EDITOR_FONT_SIZE,
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel()
            .getFontSizePanel().getValue());
    store.putObject(EDITOR_BK_COLOR,
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel()
            .getBkColorPanel().getColor());
    store.putString(CONSOLE_FONT,
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel()
            .getFontPanel().getSelectedFontName());
    store.putInt(CONSOLE_FONT_SIZE,
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel()
            .getFontSizePanel().getValue());
    store.putObject(CONSOLE_BK_COLOR,
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel()
            .getBkColorPanel().getColor());
    store.putObject(CONSOLE_FONT_COLOR,
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel()
            .getFontColorPanel().getColor());
    store.putObject(LANGUAGE,
        preferencesTabbedPane.getMiscellaneousPanel().getLanguagePanel().getCbLanguages()
            .getSelectedItem());
    store.putBoolean(EMBEDDED,
        preferencesTabbedPane.getCompilerPanel().getCbEmbedded().isSelected());
  }

  protected void restore() {
    if (store.contains(C_PATH)) {
      try {
        preferencesTabbedPane.getCompilerPanel().getCompilerPathField()
            .setText(store.getString(C_PATH));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(C_OUTPUT)) {
      try {
        preferencesTabbedPane.getCompilerPanel().getOutputPathField()
            .setText(store.getString(C_OUTPUT));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(ENCODING)) {
      try {
        preferencesTabbedPane.getMiscellaneousPanel().getCharsetPanel().getCbEncoding()
            .setSelectedItem(store.getObject(ENCODING));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(EDITOR_FONT)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getFontPanel()
            .setSelectedFontName(store.getString(EDITOR_FONT));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(EDITOR_FONT_SIZE)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getFontSizePanel()
            .setValue(store.getInt(EDITOR_FONT_SIZE));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(EDITOR_BK_COLOR)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getBkColorPanel()
            .setColor((Color) store.getObject(EDITOR_BK_COLOR));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(CONSOLE_FONT)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontPanel()
            .setSelectedFontName(store.getString(CONSOLE_FONT));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(CONSOLE_FONT_SIZE)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontSizePanel()
            .setValue(store.getInt(CONSOLE_FONT_SIZE));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(CONSOLE_BK_COLOR)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getBkColorPanel()
            .setColor((Color) store.getObject(CONSOLE_BK_COLOR));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(CONSOLE_FONT_COLOR)) {
      try {
        preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontColorPanel()
            .setColor((Color) store.getObject(CONSOLE_FONT_COLOR));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(LANGUAGE)) {
      try {
        preferencesTabbedPane.getMiscellaneousPanel().getLanguagePanel().getCbLanguages().setSelectedItem
            (store.getObject(LANGUAGE));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    if (store.contains(EMBEDDED)) {
      try {
        preferencesTabbedPane.getCompilerPanel().getCbEmbedded()
            .setSelected(store.getBoolean(EMBEDDED));
      } catch (StoreException e) {
        log.error(e.getMessage(), e);
      }
    }
    store.clear();
  }

  @Override
  public int showModal() {
    result = OPTION_CANCEL;
    store();
    getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
    buttonPanel.getBtnCancel().requestFocus();
    setVisible(true);
    if (result == OPTION_CANCEL) {
      restore();
    }
    return result;
  }
}
