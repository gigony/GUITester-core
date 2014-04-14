package guitesting.util;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.LoaderClassPath;

public class ApplicationClassLoader extends MyURLClassLoader {
  private Set<String> addedPaths;
  private ClassPool classPool;
  private ClassFileTransformer transformer;

  public ApplicationClassLoader(ClassLoader classLoader) {
    super(new URL[0], classLoader);
    addedPaths = new HashSet<String>();
    classPool = new ClassPool();
    classPool.appendClassPath(new LoaderClassPath(this));
  }

  public ApplicationClassLoader(ClassLoader classLoader, ClassFileTransformer classTransformer) {
    super(new URL[0], classLoader);
    addedPaths = new HashSet<String>();
    classPool = new ClassPool();
    classPool.appendClassPath(new LoaderClassPath(this));
    transformer = classTransformer;
  }

  public synchronized void addClassPath(File file) {
    if (file == null || !file.exists())
      return;

    try {
      URL url = file.toURI().toURL();
      addURL(url);
      addedPaths.add(file.getAbsolutePath());
      TestLogger.debug("\t%s", url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public Set<String> getAddedPaths() {
    return addedPaths;
  }

  public boolean containsPath(String path) {
    return addedPaths.contains(path);
  }

  public void addClassFolder(File file) {
    if (file == null || !file.exists())
      return;

    for (File f : file.listFiles()) {
      String name = f.getName();
      if (f.isDirectory()) {
        addClassFolder(f);
      } else {
        if (name.endsWith(".jar") || name.endsWith(".zip")) {
          addClassPath(f);
        }
      }
    }

  }

  @Override
  protected ByteBuffer instrumentClass(String name, ByteBuffer bb) {
    if (transformer != null) {
      try {
        byte[] result = transformer.transform(this, name, null, null, bb.array());
        if (result != null)
          return ByteBuffer.wrap(result);
      } catch (IllegalClassFormatException e) {
        e.printStackTrace();
      }
    }

    return bb;
  }

  @Override
  protected byte[] instrumentClass(String name, byte[] b) {
    if (transformer != null) {
      try {
        byte[] result = transformer.transform(this, name, null, null, b);
        if (result != null)
          return result;
      } catch (IllegalClassFormatException e) {
        e.printStackTrace();
      }
    }

    return b;
  }

  public ClassFileTransformer setNGetTransformer(URL classPath, String transformerName) {
    try {      
      
      //URLClassLoader cl = URLClassLoader.newInstance(new URL[] { classPath });
      Class<?> klass = Class.forName(transformerName, true, this);
      Constructor<?> constructor = klass.getConstructor(ClassLoader.class);
      transformer = (ClassFileTransformer) constructor.newInstance(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return transformer;
  }
}
