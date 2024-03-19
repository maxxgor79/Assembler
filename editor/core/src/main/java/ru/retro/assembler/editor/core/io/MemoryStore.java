package ru.retro.assembler.editor.core.io;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Maxim Gorin Date: 02.03.2024
 */
@Slf4j
public class MemoryStore implements Store {

  private Map<Integer, Object> map = new HashMap<>();

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public byte getByte(int id) throws StoreException {
    try {
      return (Byte) map.get(id);
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      throw new StoreException(e);
    }
  }

  @Override
  public void putByte(int id, byte b) {
    map.put(id, b);
  }

  @Override
  public int getInt(int id) throws StoreException {
    try {
      return (Integer) map.get(id);
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      throw new StoreException(e);
    }
  }

  @Override
  public void putInt(int id, int val) {
    map.put(id, val);
  }

  @Override
  public String getString(int id) throws StoreException {
    try {
      return (String) map.get(id);
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      throw new StoreException(e);
    }
  }

  @Override
  public void putString(int id, String s) {
    map.put(id, s);
  }

  @Override
  public Object getObject(int id) throws StoreException {
    try {
      return map.get(id);
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      throw new StoreException(e);
    }
  }

  @Override
  public void putObject(int id, Object o) {
    map.put(id, o);
  }

  @Override
  public boolean getBoolean(int id) throws StoreException {
    try {
      return (Boolean) map.get(id);
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      throw new StoreException(e);
    }
  }

  @Override
  public void putBoolean(int id, boolean b) {
    map.put(id, b);
  }

  @Override
  public boolean contains(int id) {
    return map.containsKey(id);
  }
}
