package ru.retro.assembler.editor.core.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ru.retro.assembler.editor.core.ui.components.Order;

/**
 * @Author: Maxim Gorin Date: 26.02.2024
 */
public final class UIUtils {

  private static final Map<String, String> ext2ConstMap = new HashMap<>();

  static {
    ext2ConstMap.put("xml", SyntaxConstants.SYNTAX_STYLE_XML);
    ext2ConstMap.put("java", SyntaxConstants.SYNTAX_STYLE_JAVA);
    ext2ConstMap.put("c", SyntaxConstants.SYNTAX_STYLE_C);
    ext2ConstMap.put("h", SyntaxConstants.SYNTAX_STYLE_C);
    ext2ConstMap.put("cpp", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
    ext2ConstMap.put("hpp", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
    ext2ConstMap.put("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
    ext2ConstMap.put("go", SyntaxConstants.SYNTAX_STYLE_GO);
    ext2ConstMap.put("htm", SyntaxConstants.SYNTAX_STYLE_HTML);
    ext2ConstMap.put("html", SyntaxConstants.SYNTAX_STYLE_HTML);
    ext2ConstMap.put("json", SyntaxConstants.SYNTAX_STYLE_JSON);
    ext2ConstMap.put("ini", SyntaxConstants.SYNTAX_STYLE_INI);
    ext2ConstMap.put("php", SyntaxConstants.SYNTAX_STYLE_PHP);
    ext2ConstMap.put("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
    ext2ConstMap.put("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN);
    ext2ConstMap.put("py", SyntaxConstants.SYNTAX_STYLE_PYTHON);
    ext2ConstMap.put("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
  }

  private static Map<String, Locale> localeMap = new HashMap<>();

  static {
    localeMap.put(Messages.getInstance().get(Messages.ENGLISH), Locale.ENGLISH);
    localeMap.put(Messages.getInstance().get(Messages.RUSSIAN), Locale.forLanguageTag("ru-RU"));
  }

  private static Map<String, String> languageMap = new HashMap<>();

  static {
    languageMap.put(Locale.ENGLISH.getLanguage(), Messages.getInstance().get(Messages.ENGLISH));
    languageMap.put(Locale.forLanguageTag("ru-RU").getLanguage(),
        Messages.getInstance().get(Messages.RUSSIAN));
  }

  private UIUtils() {

  }

  public static void putExt(@NonNull String key, @NonNull String value) {
    ext2ConstMap.put(key, value);
  }

  public static int getLine(@NonNull JTextArea textArea) throws BadLocationException {
    return textArea.getLineOfOffset(textArea.getCaretPosition());
  }

  public static int getColumn(@NonNull JTextArea textArea, int line) throws BadLocationException {
    return textArea.getCaretPosition() - textArea.getLineStartOffset(line);
  }

  public static RSyntaxTextArea createTextArea() {
    final RSyntaxTextArea textArea = new RSyntaxTextArea(40, 80);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    textArea.setCodeFoldingEnabled(true);
    return textArea;
  }

  public static RSyntaxTextArea createTextArea(@NonNull final File file) {
    return createTextArea(file.getAbsolutePath());
  }

  public static RSyntaxTextArea createTextArea(@NonNull final String file) {
    String extension = FilenameUtils.getExtension(file);
    final RSyntaxTextArea textArea = new RSyntaxTextArea(40, 80);
    String syntaxConst = ext2ConstMap.get(extension);
    if (syntaxConst == null) {
      syntaxConst = SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80;
    }
    textArea.setSyntaxEditingStyle(syntaxConst);
    textArea.setCodeFoldingEnabled(true);
    return textArea;
  }

  public static Font createFont(@NonNull String fontName, int size) {
    return new Font(fontName, Font.PLAIN, size);
  }

  public static Locale toLocale(@NonNull final String language) {
    return localeMap.get(language);
  }

  public static String toLanguage(@NonNull Locale locale) {
    return languageMap.get(locale.getLanguage());
  }

  public static boolean equals(@NonNull Locale locale1, @NonNull Locale locale2) {
    return locale1.getLanguage().equals(locale2.getLanguage());
  }

  public static void updateUIManager() {
    UIManager.put("ColorChooser.okText", Messages.getInstance().get(Messages.OK));
    UIManager.put("ColorChooser.cancelText", Messages.getInstance().get(Messages.CANCEL));
    UIManager.put("ColorChooser.resetText", Messages.getInstance().get(Messages.RESET));
    UIManager.put("ColorChooser.sampleText", Messages.getInstance().get(Messages.SAMPLE));
    UIManager.put("ColorChooser.previewText", Messages.getInstance().get(Messages.PREVIEW));
    UIManager.put("ColorChooser.swatchesRecentText", Messages.getInstance().get(Messages.RECENT));
    UIManager.put("OptionPane.yesButtonText", Messages.getInstance().get(Messages.YES));
    UIManager.put("OptionPane.noButtonText", Messages.getInstance().get(Messages.NO));
  }

  public static void sort(@NonNull List<? extends Order> list) {
    Collections.sort(list, (o1, o2) -> {
      if (o1.order() > o2.order()) {
        return 1;
      }
      if (o1.order() < o2.order()) {
        return -1;
      }
      return 0;
    });
  }

  ;
}
