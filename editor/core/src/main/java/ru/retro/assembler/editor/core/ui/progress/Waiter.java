package ru.retro.assembler.editor.core.ui.progress;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Waiter {
    private CountDownLatch latch;

    private class WaiterDialog extends JDialog {
        private final JProgressBar progressResponse = new JProgressBar();

        public WaiterDialog(Window frame, String title, boolean modal) {
            super(frame, title);
            setModal(modal);
            initComponents();
            initListeners();
        }

        public WaiterDialog() {
            this(null, "", true);
        }

        private void initComponents() {
            setUndecorated(true);
            progressResponse.setIndeterminate(true);
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createEtchedBorder(Color.white, new Color(142, 142, 142)));
            GridBagLayout gb = new GridBagLayout();
            panel.setLayout(gb);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.weighty = 1.0;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gb.setConstraints(progressResponse, gbc);
            panel.add(progressResponse);
            this.setLayout(new BorderLayout());
            this.add(panel, BorderLayout.CENTER);
            pack();
        }

        private void initListeners() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (Waiter.this.latch != null) {
                        Waiter.this.latch.countDown();
                    }
                }

                @Override
                public void windowOpened(WindowEvent e) {
                    if (Waiter.this.latch != null) {
                        Waiter.this.latch.countDown();
                    }
                }
            });
        }

    }

    private final Window frame;

    private final WaiterDialog instance;

    public Waiter(Window frame) {
        this.frame = frame;
        instance = new WaiterDialog(frame, null, true);
    }

    public void show() throws InterruptedException, InvocationTargetException {
        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            instance.setLocationRelativeTo(frame);
            instance.setVisible(true);
        });
        //latch.await();
        latch = null;
    }

    public void hide() {
        latch = new CountDownLatch(1);
        try {
            SwingUtilities.invokeLater(() -> {
                instance.setVisible(false);
                instance.dispose();
            });
            //latch.await();
            latch = null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
