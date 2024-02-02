package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Block {

  @Getter
  @Setter
  protected int blockId;

  public abstract void read(@NonNull InputStream is) throws IOException;

  public abstract void write(@NonNull OutputStream os) throws IOException;

  public void export(@NonNull OutputStream os) throws IOException {
  }
}
