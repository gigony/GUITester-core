package guitesting.util;

import java.net.URISyntaxException;
import java.net.URL;

import guitesting.engine.strategy.AbstractStrategy;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class PluginClassLoaderTest {

  @Test
  public void test() {
    PropertyConfigurator.configure("/Users/gigony/git/guitester/GUITester/src/log4j.properties");
    PluginClassLoader loader = new PluginClassLoader(Thread.currentThread().getContextClassLoader());
    loader.addPluginFolder("/Users/gigony/Documents/workspace/eclipse/GUITesterPlugins/bin");
    loader.addPluginFolder("/Users/gigony/Documents/workspace/eclipse/GUITesterPlugins/plugins");
    for (String className : loader.getAddedPlugins()) {
      try {
        System.out.println(className);
        Class<?> cl = loader.loadClass(className);
        System.out.println(AbstractStrategy.class.isAssignableFrom(cl));
        System.out.println(cl.getSuperclass());
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Test
  public void classPathTest() {
    System.out.println(((URL)Thread.currentThread().getContextClassLoader().getResource("a.txt")));

  }

  public static void main(String[] args) {
    // new PluginClassLoaderTest().test();
    
    new PluginClassLoaderTest().classPathTest();
  }

}
