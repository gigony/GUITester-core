package guitesting.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;

//from SwingLabs Demos(TreeDemoIconValues.java)
public class LazyLoadingIconValue implements IconValue {
  private Class<?> baseClass;
  private StringValue keyToFileName;
  private Map<Object, Icon> iconCache;
  private Icon fallbackIcon;

  public LazyLoadingIconValue(Class<?> baseClass, StringValue sv, String fallbackName) {
    this.baseClass = baseClass;
    iconCache = new HashMap<Object, Icon>();
    this.keyToFileName = sv;
    fallbackIcon = loadFromResource(fallbackName);
  }

  // IconValue based on node value
  /**
   * {@inheritDoc}
   * <p>
   * 
   * Implemented to return a Icon appropriate for the given node value. The icon is loaded (and later cached) as a
   * resource, using a lookup key created by a StringValue.
   * 
   */
  @Override
  public Icon getIcon(Object value) {
    String key = keyToFileName.getString(value);
    Icon icon = iconCache.get(key);
    if (icon == null && !"".equals(key)) { // modified by pureku
      icon = loadIcon(key);
    }
    if (icon == null) {
      icon = fallbackIcon;
    }
    return icon;
  }

  private Icon loadIcon(String key) {
    Icon icon = loadFromResource(key);
    if (icon != null) {
      iconCache.put(key, icon);
    } else
      iconCache.put(key, fallbackIcon); // added by pureku
    return icon;
  }

  protected Icon loadFromResource(String name) {
    URL url = baseClass.getResource("resources/images/" + name);
    if (url == null)
      return null;
    try {
      BufferedImage image = ImageIO.read(url);
      if (image.getHeight() > 30) {
        image = GraphicsUtilities.createThumbnail(image, 16);
      }
      return new ImageIcon(image);
    } catch (IOException e) {
    }
    return null;
  }

}
