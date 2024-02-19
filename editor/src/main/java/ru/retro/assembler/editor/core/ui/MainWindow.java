package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.settings.AppSettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.PAGE_START);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(createMenuFile());
        bar.add(createMenuEdit());
        bar.add(createMenuBuild());
        bar.add(createMenuTools());
        bar.add(createMenuHelp());
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
        JButton btn = createToolButton("/icons/new.png");
        btn.setToolTipText("New file");
        return btn;
    }

    protected JButton createBtnSave() {
        JButton btn = createToolButton("/icons/save.png");
        btn.setToolTipText("Save file");
        return btn;
    }

    protected JButton createBtnRefresh() {
        JButton btn = createToolButton("/icons/refresh.png");
        btn.setToolTipText("Refresh all files");
        return btn;
    }

    protected JButton createBtnCompile() {
        JButton btn = createToolButton("/icons/compile.png");
        btn.setToolTipText("Compile project");
        return btn;
    }

    protected JButton createBtnOpen() {
        JButton btn = createToolButton("/icons/open.png");
        btn.setToolTipText("Open file");
        return btn;
    }

    protected JButton createToolButton(@NonNull String path) {
        final JButton btn = new JButton();
        btn.setMinimumSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
        btn.setSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
        BufferedImage icon = null;
        if (path != null) {
            try {
                byte[] iconData = IOUtils.toByteArray(this.getClass().getResourceAsStream(path));
                icon = ImageIO.read(new ByteArrayInputStream(iconData));
                btn.setIcon(new ImageIcon(icon));
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
