package guitesting.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends URLClassLoader {
  private Set<String> addedPlugins;

  public PluginClassLoader(ClassLoader classLoader) {

    super(new URL[0], classLoader);
    addedPlugins = new HashSet<String>();
  }

  private synchronized void addCompressedPluginFiles(File parentFolder) {
    if (parentFolder == null || !parentFolder.exists())
      return;

    for (File f : parentFolder.listFiles()) {
      String name = f.getName();
      if (f.isDirectory()) {
        addCompressedPluginFiles(f);
      } else {
        if (name.endsWith(".jar") || name.endsWith(".zip")) {
          addJarClassFile(f);
        }
      }
    }

  }

  public synchronized void addPluginFolder(String filePath) {
    if (filePath == null || "".equals(filePath))
      return;
    File file = new File(filePath);
    addPluginFolder(file);
  }

  public synchronized void addPluginFolder(File file) {
    if (file == null || !file.exists())
      return;

    try {
      addURL(file.toURI().toURL());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    for (File f : file.listFiles()) {
      String name = f.getName();
      if (!f.isDirectory()) {
        if (name.endsWith(".class")) {
          addClassFile(f, "");
        }
      }
    }
    addCompressedPluginFiles(file);

  }

  private void addPluginFolder(String filePath, String packageName) {

    File file = new File(filePath);

    for (File f : file.listFiles()) {
      String name = f.getName();
      if (f.isDirectory()) {
        String newPackageName = "".equals(packageName) ? name : packageName + "." + name;
        addPluginFolder(f.getAbsolutePath(), newPackageName);
      } else {
        if (name.endsWith(".class")) {
          addClassFile(f, packageName);
        } else if (name.endsWith(".jar")) {
          addJarClassFile(f);
        }
      }
    }

  }

  private void addClassFile(File f, String packageName) {

    TestLogger.debug("\tPluginClassLoader.addClassFile: %s", f.getAbsoluteFile());
    String className = getClassName(f, packageName);
    addedPlugins.add(className);

    // try {
    // String className = getClassName(f, packageName);
    //
    // if(loadedClasses.contains(className))
    // return;
    //
    // InputStream input = new FileInputStream(f);
    // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    // int data = input.read();
    // while (data != -1) {
    // buffer.write(data);
    // data = input.read();
    // }
    // input.close();
    // byte[] classData = buffer.toByteArray();
    // defineClass(className, classData, 0, classData.length);
    // loadedClasses.add(className);
    // TestLogger.debug("\t%s (%s) is loaded.", className, f.getAbsolutePath());
    // } catch (Throwable e) {
    // TestLogger.warn("Failure occurred while adding plugins: %s\n\t(%s):%s", f.getAbsolutePath(),
    // e.getClass().getName(),e.getMessage());
    // }
  }

  private String getClassName(File f, String packageName) {
    String className = f.getName().replace(".class", "");
    if ("".equals(packageName)) {
      return className;
    } else {
      return packageName + "." + className;
    }
  }

  public Set<String> getAddedPlugins() {
    return addedPlugins;
  }

  public boolean containsClass(String className) {
    return addedPlugins.contains(className);
  }

  private void addJarClassFile(File f) {
    TestLogger.debug("\tPluginClassLoader.addJarClassFile: %s", f.getAbsoluteFile());

    try {
      addURL(f.toURI().toURL());
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

    JarFile jarFile = null;
    try {
      jarFile = new JarFile(f);
      Enumeration<JarEntry> entries = jarFile.entries();
      JarEntry entry = null;
      while (entries.hasMoreElements()) {
        entry = entries.nextElement();

        if (entry.getName().endsWith(".class")) {
          String className = getFullClassNameFromPathName(entry.getName());

          if (getSimpleClassName(className).equals(className)) {
            addedPlugins.add(className);
            TestLogger.debug("\t\tAdded class: %s (%s)", className, f.getAbsolutePath());
          }
        }
      }

    } catch (Throwable e) {
      e.printStackTrace();
      TestLogger.warn("Failure occurred while adding plugins: %s\n\t%s", f.getAbsolutePath(), e.getMessage());
    } finally {
      if (jarFile != null) {
        try {
          jarFile.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }
  }

  private String getFullClassNameFromPathName(String pathName) {
    return pathName.replace(".class", "").replace("/", ".");
  }

  private String getSimpleClassName(String className) {
    int pivot = className.lastIndexOf(".");
    if (pivot == -1)
      return className;
    else
      return className.substring(pivot + 1);
  }

  public Object newInstance(Class<?> cl) {
    try {
      Constructor<?> constructor = cl.getConstructor(new Class<?>[0]);
      return constructor.newInstance(new Object[] { null });
    } catch (Exception e) {
      TestLogger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

}
