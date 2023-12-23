package ru.zxspectrum.io.tap;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maxim Gorin
 */
@Slf4j
@ToString
abstract public class Block implements TapElementReader, TapElementWriter {


  @Setter
  @Getter
  protected int blockLength;

  @Getter
  @NonNull
  protected Flag flag;


  @Getter
  protected int checkSum;

  protected abstract int calcCheckSum();

  public void setFlag(@NonNull Flag flag) {
    if (this.flag == flag) {
      return;
    }
    this.flag = flag;
    checkSum = calcCheckSum();
  }

  public abstract int size();
}
