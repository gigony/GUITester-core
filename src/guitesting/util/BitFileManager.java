/*******************************************************************************
 * Copyright (c) 2010-2011, Gigon Bae
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  
 *     1. Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *     
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *     3. Neither the name of this project nor the names of its contributors may be
 *        used to endorse or promote products derived from this software without
 *        specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package guitesting.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BitFileManager {
  static final int BitPerByte = 8;
  static final int BitShift = 3;

  transient File file = null;
  transient FileChannel fc = null;
  long initialSize = 0;
  long currentSize = 0;
  ByteBuffer byteBuffer = null;
  ByteBuffer emptyBB;

  public BitFileManager(String filePath, long initByteSize) {
    file = new File(filePath);
    initialSize = initByteSize;
  }

  public void deleteFile() {
    if (file.exists() && !file.delete()) {
      throw new RuntimeException();
    }
  }

  public void open() {
    try {
      emptyBB = ByteBuffer.allocateDirect(1);
      emptyBB.put(0, (byte) 0);
      System.out.println("#file:" + file.getAbsolutePath());
      if (!file.exists())
        file.createNewFile();
      fc = new RandomAccessFile(file, "rw").getChannel();

      currentSize = fc.size();

      if (currentSize < initialSize) {
        currentSize = initialSize;
        fc.write(emptyBB, currentSize);
      }
      byteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, currentSize);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void clear() {
    for (int i = 0; i < currentSize; i++)
      byteBuffer.put(i, (byte) 0);
  }

  public boolean get(int bitIndex) {
    int index = bitIndex >> BitShift;
    if (index >= currentSize)
      return false;
    return (byteBuffer.get(index) & (1 << (bitIndex % BitPerByte))) != 0;
  }

  public void set(int bitIndex, boolean value) {
    int index = bitIndex >> BitShift;
    expand(index);
    byte bit = (byte) (1 << (bitIndex % BitPerByte));
    byte byteValue = byteBuffer.get(index);
    if (value) {
      byteValue |= bit;
      byteBuffer.put(index, byteValue);
    } else {
      byteValue &= ~bit;
      byteBuffer.put(index, byteValue);
    }
  }

  private void expand(int index) {
    // System.out.println("index:"+index+" currentSize"+currentSize);
    if (index >= currentSize) {
      while (index >= currentSize) {
        currentSize <<= 1;
        System.out.println("currentSize" + currentSize);
      }
      try {
        byteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, currentSize);
        byteBuffer.position((int) currentSize);
        // System.out.println(currentSize+" ," +byteBuffer.limit());
      } catch (IOException e) {
        e.printStackTrace();
      }
      // System.out.println("### extend memory-mapped file size:" + currentSize);
    }
  }

  public void close() {
    try {
      if (fc != null) {
        fc.close();
        fc = null;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    BitFileManager bitFile = new BitFileManager("test.txt", 10);
    bitFile.deleteFile();
    bitFile.open();
    // bitFile.set(1000000, true);
    // System.out.println(bitFile.get(1000000000));

    for (int i = 0; i < 300 * 300 * 300; i = i + 2) {
      bitFile.set(i, true);
    }
    for (int i = 0; i < 10000; i++) {
      System.out.print(i + ": ");
      if (bitFile.get(i))
        System.out.println("O");
      else
        System.out.println("X");
    }

    bitFile.close();

  }

}
