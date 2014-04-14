package guitesting.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class MyObjectInputStream extends ObjectInputStream {
  ClassLoader myLoader = null;

  public MyObjectInputStream(InputStream theStream, ClassLoader newLoader) throws IOException {
    super(theStream);
    myLoader = newLoader;
  }

  public MyObjectInputStream(InputStream theStream) throws IOException {
    super(theStream);
  }

  protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
    if (myLoader == null)
      return super.resolveClass(desc);

    Class theClass = null;
    try {
      theClass = Class.forName(desc.getName(), true, myLoader);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return theClass;
  }
}