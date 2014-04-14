package guitesting.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyValueStoreTest {

  @Test
  public void testParsingArgs() {
    KeyValueStore store = new KeyValueStore();
    store.setArgs("a=1,b=3,b=4,c=5,,");
    for(Object key:store.keySet()){
      System.out.println(String.format("%s => %s",key,store.get(key)));
    }
    assertEquals("error","a=1,b=4,c=5",store.toString());    
  }
}
