package guitesting.util;

import sun.security.util.BitArray;

public class MyBitArray {
  BitArray[] bitArrays;
  final static int INTMAX = 0x7ffffff0;
  final static long INTMAXLONG = 0x7ffffff0;

  public MyBitArray(long length) {
    int arraySize = (int) (length / INTMAXLONG) + 1;
    bitArrays = new BitArray[arraySize];
    for (int i = 0; i < arraySize; i++) {
      bitArrays[i] = new BitArray(INTMAX);
    }
  }

  public void set(long index, boolean value) {
    int arrayIdx = getArrayIdx(index);
    int elemIdx = getElemIdx(index);
    bitArrays[arrayIdx].set(elemIdx, value);
  }

  public boolean get(long index) {
    int arrayIdx = getArrayIdx(index);
    int elemIdx = getElemIdx(index);
    return bitArrays[arrayIdx].get(elemIdx);
  }

  private int getArrayIdx(long index) {
    return (int) (index / INTMAXLONG);
  }

  private int getElemIdx(long index) {
    return (int) (index % INTMAXLONG);
  }

}
