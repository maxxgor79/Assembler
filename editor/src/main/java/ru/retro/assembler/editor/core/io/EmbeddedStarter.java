package ru.retro.assembler.editor.core.io;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Slf4j
public class EmbeddedStarter implements Runnable {
    private List<String> argList;

    private MainWindow mainWindow;

    private static Class getAssembler() {
        try {
            final Class clazz = Class.forName("ru.assembler.zxspectrum.Z80Assembler");
            return clazz;
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public EmbeddedStarter(@NonNull final List<String> argList, @NonNull final MainWindow mainWindow) {
        this.argList = argList;
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        try {
            final Class clazz = getAssembler();
            if (clazz == null) {
                JOptionPane.showMessageDialog(mainWindow,
                        Messages.get(Messages.EMBEDDED_NOT_FOUND), Messages.get(Messages.ERROR)
                        , JOptionPane.ERROR_MESSAGE, ResourceUtils.getErrorIcon());
                return;
            }
            final Method method = clazz.getMethod("entry", Collection.class);
            method.invoke(null, argList);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            JOptionPane.showMessageDialog(mainWindow,
                    t.getMessage(), Messages.get(Messages.ERROR), JOptionPane.ERROR_MESSAGE
                    , ResourceUtils.getErrorIcon());
        }
    }
}
