package ru.retro.assembler.editor.core.io;

/**
 * @Author: Maxim Gorin Date: 02.03.2024
 */
public interface Store {

  void clear();

  byte getByte(int id) throws StoreException;

  void putByte(int id, byte b);

  int getInt(int id) throws StoreException;

  void putInt(int id, int val);

  String getString(int id) throws StoreException;

  void putString(int id, String s);

  Object getObject(int id) throws StoreException;

  void putObject(int id, Object o);

  boolean getBoolean(int id) throws StoreException;

  void putBoolean(int id, boolean b);

  boolean contains(int id);

}
