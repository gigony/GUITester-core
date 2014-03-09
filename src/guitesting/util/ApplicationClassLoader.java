package guitesting.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class ApplicationClassLoader extends URLClassLoader {
  private Set<String> addedPaths;

  public ApplicationClassLoader(ClassLoader classLoader) {
    super(new URL[0], classLoader);
    addedPaths = new HashSet<String>();
  }

  public synchronized void addClassPath(File file) {
    if (file == null || !file.exists())
      return;

    try {
      addURL(file.toURI().toURL());
      addedPaths.add(file.getAbsolutePath());
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
}
