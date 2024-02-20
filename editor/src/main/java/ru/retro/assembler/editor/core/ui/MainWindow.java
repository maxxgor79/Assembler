package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public class MainWindow extends JFrame {
    protected static final String TITLE = "Retro IDE";

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

    public MainWindow() {
        init();
    }

    protected void init() {
        setTitle(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.75)));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    protected void initComponents() {
        try {
            final Image image = ResourceUtils.loadImage("/icon16x16/chip.png");
            setIconImage(image);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.PAGE_START);
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
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        return file;
    }

    protected JMenu createMenuEdit() {
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic('E');
        return edit;
    }

    protected JMenu createMenuBuild() {
        JMenu edit = new JMenu("Build");
        edit.setMnemonic('B');
        return edit;
    }

    protected JMenu createMenuTools() {
        JMenu tools = new JMenu("Tools");
        tools.setMnemonic('T');
        return tools;
    }

    protected JMenu createMenuHelp() {
        JMenu help = new JMenu("Help");
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
        btn.setToolTipText("New file");
        return btn;
    }

    protected JButton createBtnSave() {
        JButton btn = createToolButton("/icon32x32/save.png");
        btn.setToolTipText("Save file");
        return btn;
    }

    protected JButton createBtnRefresh() {
        JButton btn = createToolButton("/icon32x32/refresh.png");
        btn.setToolTipText("Refresh all files");
        return btn;
    }

    protected JButton createBtnCompile() {
        JButton btn = createToolButton("/icon32x32/compile.png");
        btn.setToolTipText("Compile project");
        return btn;
    }

    protected JButton createBtnOpen() {
        JButton btn = createToolButton("/icon32x32/open.png");
        btn.setToolTipText("Open file");
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


    public void set(@NonNull AppSettings settings) {
        setLocation(settings.getMainFramePosX(), settings.getMainFramePosY());
        setSize(settings.getMainFrameWidth(), settings.getMainFrameHeight());
    }

    public void get(@NonNull AppSettings settings) {
        settings.setMainFramePosX(getLocation().x);
        settings.setMainFramePosY(getLocation().y);
        settings.setMainFrameWidth(getWidth());
        settings.setMainFrameHeight(getHeight());
    }
}
