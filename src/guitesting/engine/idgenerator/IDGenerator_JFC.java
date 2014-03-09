/*******************************************************************************
 * Copyright (c) 2010-2014 Gigon Bae
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package guitesting.engine.idgenerator;

import guitesting.model.ComponentModel;
import guitesting.model.event.EventModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class IDGenerator_JFC extends IDGenerator {
  final static List<String> structuralPropertyList = Arrays.asList("class", "actioncommand", "actionlisteners",
      "mouselisteners");

  HashFunction hf = Hashing.md5();

  @Override
  public HashCode getComponentHash(ComponentModel src, HashCode parentHashCode) {
    Hasher hasher = hf.newHasher();
    if (parentHashCode != null)
      hasher.putBytes(parentHashCode.asBytes());

    Map<String, String> srcProperties = src.getProperties();
    hasher.putString(srcProperties.get("title"), Charsets.UTF_8);

    for (String key : structuralPropertyList) {
      String value = srcProperties.get(key);
      if (value != null)
        hasher.putString(value, Charsets.UTF_8);
      else
        hasher.putBoolean(false);
    }
    return hasher.hash();
  }

  @Override
  public HashCode getEventHash(EventModel src) {
    Hasher hasher = hf.newHasher();

    String id = src.getComponentModel().get("id");
    if (id != null)
      hasher.putString(id, Charsets.UTF_8);
    else
      hasher.putBoolean(false);

    hasher.putInt(src.getValueHash());

    String componentIdx = src.getComponentModel().get("componentIndex");
    if (componentIdx != null)
      hasher.putString(componentIdx, Charsets.UTF_8);
    else
      hasher.putBoolean(false);

    return hasher.hash();
  }

  @Override
  public HashCode add(HashCode o1, HashCode o2) {
    Hasher hasher = hf.newHasher();
    return hasher.putBytes(o1.asBytes()).putBytes(o2.asBytes()).hash();

  }
}
