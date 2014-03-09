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

public class Pair<K, V> {
  private K key;
  private V value;

  public Pair(K key, V value) {
    this.setKey(key);
    this.setValue(value);
  }

  public void setKey(K key) {
    this.key = key;
  }

  public K getKey() {
    return key;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public V getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Pair) {
      if (key.equals(((Pair) obj).getKey()) && value.equals(((Pair) obj).getValue()))
        return true;
    }
    return false;

  }

  @Override
  public int hashCode() {
    return key.hashCode() * 121 + value.hashCode();
  }

}
