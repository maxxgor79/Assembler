package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;

public abstract class Block implements TzxElementWriter, TzxElementReader {

  @Getter
  @Setter
  protected int blockId;

  @Override
  public void export(@NonNull OutputStream os) throws IOException {
  }
}
