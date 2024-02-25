package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.console.ConsolePanel;
import ru.retro.assembler.editor.core.ui.preferences.PreferencesDialog;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author: Maxim Gorin Date: 19.02.2024
 */
@Slf4j
public class MainWindow extends JFrame {

    protected static final String TITLE = Messages.get(Messages.CAPTION);

    protected static final int ICON_WIDTH = 32;

    protected static final int ICON_HEIGHT = 32;

    @Getter
    private JButton btnNew;

    @Getter
    private JButton btnOpen;

    @Getter
    private JButton btnSave;

    @Getter
    private JButton btnRefresh;

    @Getter
    private JButton btnCompile;

    @Getter
    private FileMenuItems fileMenuItems;

    @Getter
    private EditMenuItems editMenuItems;

    @Getter
    private HelpMenuItems helpMenuItems;

    @Getter
    private BuildMenuItems buildMenuItems;

    @Getter
    private ToolsMenuItems toolsMenuItems;

    @Getter
    private JSplitPane splitPane;

    @Getter
    private ConsolePanel console;

    @Getter
    private SourceTabbedPane sourceTabbedPane;

    private StatusPanel statusPanel;

    private AboutDialog aboutDialog;

    private PreferencesDialog preferencesDialog;

    private URI helpUri;

    public MainWindow() {
        init();
    }

    protected void init() {
        setTitle(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.75)));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        initListeners();
    }

    protected void initComponents() {
        try {
            setIconImage(ResourceUtils.loadImage("/icon16x16/chip.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.NORTH);
        console = createConsole();
        sourceTabbedPane = new SourceTabbedPane();
        sourceTabbedPane.add(new Source(new File("noname1.asm"), "mov eax,ebx"));
        sourceTabbedPane.add(new Source(new File("noname2.asm"), "daa"));
        //tabbedPane.addTab("Test", new EditorPanel());
        splitPane = createSplitPane(sourceTabbedPane, console);
        add(splitPane, BorderLayout.CENTER);
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JSplitPane createSplitPane(JComponent component1, JComponent component2) {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, component1, component2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.1);
        return splitPane;
    }

    private ConsolePanel createConsole() {
        return new ConsolePanel();
    }

    protected JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu menu;
        bar.add(menu = createMenuFile());
        fileMenuItems = new FileMenuItems(menu);
        bar.add(menu = createMenuEdit());
        editMenuItems = new EditMenuItems(menu);
        bar.add(menu = createMenuBuild());
        buildMenuItems = new BuildMenuItems(menu);
        bar.add(menu = createMenuTools());
        toolsMenuItems = new ToolsMenuItems(menu);
        bar.add(menu = createMenuHelp());
        helpMenuItems = new HelpMenuItems(menu);
        return bar;
    }

    protected JMenu createMenuFile() {
        JMenu file = new JMenu(Messages.get(Messages.FILE));
        file.setMnemonic('F');
        return file;
    }

    protected JMenu createMenuEdit() {
        JMenu edit = new JMenu(Messages.get(Messages.EDIT));
        edit.setMnemonic('E');
        return edit;
    }

    protected JMenu createMenuBuild() {
        JMenu edit = new JMenu(Messages.get(Messages.BUILD));
        edit.setMnemonic('B');
        return edit;
    }

    protected JMenu createMenuTools() {
        JMenu tools = new JMenu(Messages.get(Messages.TOOLS));
        tools.setMnemonic('T');
        return tools;
    }

    protected JMenu createMenuHelp() {
        JMenu help = new JMenu(Messages.get(Messages.HELP));
        help.setMnemonic('H');
        return help;
    }

    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(btnNew = createBtnNew());
        toolBar.addSeparator();
        toolBar.add(btnOpen = createBtnOpen());
        toolBar.add(btnSave = createBtnSave());
        toolBar.add(btnRefresh = createBtnRefresh());
        toolBar.addSeparator();
        toolBar.add(btnCompile = createBtnCompile());
        return toolBar;
    }

    protected JButton createBtnNew() {
        JButton btn = createToolButton("/icon32x32/new.png");
        btn.setToolTipText(Messages.get(Messages.NEW_FILE));
        return btn;
    }

    protected JButton createBtnSave() {
        JButton btn = createToolButton("/icon32x32/save.png");
        btn.setToolTipText(Messages.get(Messages.SAVE_FILE));
        return btn;
    }

    protected JButton createBtnRefresh() {
        JButton btn = createToolButton("/icon32x32/refresh.png");
        btn.setToolTipText(Messages.get(Messages.RELOAD_ALL_FILES));
        return btn;
    }

    protected JButton createBtnCompile() {
        JButton btn = createToolButton("/icon32x32/compile.png");
        btn.setToolTipText(Messages.get(Messages.COMPILE_FILE));
        return btn;
    }

    protected JButton createBtnOpen() {
        JButton btn = createToolButton("/icon32x32/open.png");
        btn.setToolTipText(Messages.get(Messages.OPEN_FILE));
        return btn;
    }

    protected JButton createToolButton(@NonNull String path) {
        final JButton btn = new JButton();
        btn.setMinimumSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
        btn.setSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
        if (path != null) {
            try {
                btn.setIcon(ResourceUtils.loadIcon(path));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return btn;
    }

    private void initListeners() {
        toolsMenuItems.getMiPreferences().addActionListener(l -> {
            preferencesDialog = getPreferencesDialogInstance();
            preferencesDialog.showModal();
        });
        helpMenuItems.getMiAbout().addActionListener(l -> {
            aboutDialog = getAboutDialogInstance();
            aboutDialog.showModal();
        });
        helpMenuItems.getMiHelp().addActionListener(l -> {
            try {
                Desktop.getDesktop().browse(helpUri);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    protected PreferencesDialog getPreferencesDialogInstance() {
        if (preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog(this);
        }
        return preferencesDialog;
    }

    protected AboutDialog getAboutDialogInstance() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(this);
        }
        return aboutDialog;
    }

    public void apply(@NonNull final AppSettings settings) {
        setLocation(settings.getMainFramePosX(), settings.getMainFramePosY());
        setSize(settings.getMainFrameWidth(), settings.getMainFrameHeight());
        setExtendedState(settings.getState());
        splitPane.setDividerLocation(settings.getDividerLocation());
        try {
            helpUri = new URI(settings.getHelpUri());
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        if (settings.getCompilerPath() != null) {
            getPreferencesDialogInstance().getPreferencesTabbedPane().getPreferencesPanel().getCompilerPathField()
                    .setText(settings.getCompilerPath());
        }
        if (settings.getOutputDirectory() != null) {
            getPreferencesDialogInstance().getPreferencesTabbedPane().getPreferencesPanel().getOutputPathField()
                    .setText(settings.getOutputDirectory());
        }
        getAboutDialogInstance().setMajorVersion(settings.getMajorVersion());
        getAboutDialogInstance().setMinorVersion(settings.getMinorVersion());
    }

    public void store(@NonNull final AppSettings settings) {
        final Point pt = this.getLocation();
        settings.setMainFramePosX(pt.x);
        settings.setMainFramePosY(pt.y);
        settings.setMainFrameWidth(getWidth());
        settings.setMainFrameHeight(getHeight());
        settings.setState(getExtendedState());
        settings.setDividerLocation(splitPane.getDividerLocation());
        settings.setCompilerPath(getPreferencesDialogInstance().getPreferencesTabbedPane().getPreferencesPanel()
                .getCompilerPathField().getText());
        settings.setOutputDirectory(getPreferencesDialogInstance().getPreferencesTabbedPane().getPreferencesPanel()
                .getOutputPathField().getText());
    }
}