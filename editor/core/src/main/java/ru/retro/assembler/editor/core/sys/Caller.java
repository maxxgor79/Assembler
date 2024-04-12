package ru.retro.assembler.editor.core.sys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Exec.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class Caller {

  private static final String METHOD_NAME = "entry";

  public static <E> void call(@NonNull Class clazz, @NonNull Collection<E> args)
      throws CallException {
    call(clazz.getName(), args);
  }

  public static <E> void call(@NonNull String classPath, @NonNull Collection<E> args)
      throws CallException {
    try {
      final Class clazz = Class.forName(classPath);
      final Method method = clazz.getMethod(METHOD_NAME, Collection.class);
      method.invoke(null, args);
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
      throw new CallException(e);
    }
  }
}
