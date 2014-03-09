/*******************************************************************************
 * All rights reserved.
 * Copyright (c) 2010-2011, Gigon Bae
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

import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.model.WindowModel;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

public class JFCUtil {

  public static String getComponentTitle(Accessible component) {
    if (component == null)
      return "null";

    AccessibleContext aContext = component.getAccessibleContext();
    if (aContext == null) {
      return "null(noAccessibleContext)";
    }

    // if JTabbedPane, concatenate tab strings
    if (component instanceof JTabbedPane) {
      JTabbedPane tabPane = (JTabbedPane) component;
      int tabCount = tabPane.getTabCount();
      StringBuilder tabTitles = new StringBuilder();
      for (int i = 0; i < tabCount; i++) {
        tabTitles.append(tabPane.getTitleAt(i));
        tabTitles.append("|");
      }
      return tabTitles.toString();
    }

    String title = null;
    try {
      title = aContext.getAccessibleName();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (title != null && !title.equals(""))
      return title;

    // programmatically call
    title = (String) IOUtil.invokeGetMethod(component, "getName");
    if (title != null && !title.equals(""))
      return title;

    // if bean's name doesn't exist
    title = (String) IOUtil.invokeGetMethod(component, "getTitle");
    if (title != null && !title.equals(""))
      return title;

    // 2011-03-17 deleted since texts in textfields are changed frequently.
    // title = (String) invokeGetMethod(component, "getText");
    // if (title != null && !title.equals(""))
    // return title;

    if ("".equals(component.getClass().getSimpleName())) // if inner class
    {
      title = component.getClass().getSuperclass().getSimpleName() + "(inner class)";
      return title;
    }

    title = JFCUtil.getIconName(component); // if icon is exist

    if (title != null && !title.equals(""))
      return title;

    if (component instanceof Component) { // if glass pane
      Container parent = ((Component) component).getParent();
      // if glass pane
      if (parent instanceof JRootPane && ((JRootPane) parent).getGlassPane().equals(component)) {
        title = "glass pane";
        return title;
      }

      // if content pane
      if (parent instanceof JLayeredPane
          && ((JLayeredPane) parent).getLayer((Component) component) == JLayeredPane.FRAME_CONTENT_LAYER) {
        Container grandParent = parent.getParent();
        if (grandParent instanceof JRootPane && ((JRootPane) grandParent).getContentPane().equals(component)) {
          title = "content pane";
          return title;
        }
      }
      title = component.getClass().getSimpleName();
      if ("".equals(title)) // if inner class
      {
        title = component.getClass().getSuperclass().getSimpleName() + "(inner class)";
        return title;
      }

      // title[index]
      Container parentComponent = ((Component) component).getParent();
      if (parentComponent != null) {
        Component[] components = parentComponent.getComponents();
        int index = 0;
        for (Component comp : components) {
          if (comp != null && comp.equals(component)) {
            title = String.format("%s[%d]", title, index);
            return title;
          }
          index++;
        }
      }
      return title;
      // 2011-03-17 deleted since x,y,width, and height can change by look&feel
      // title = String.format("%s[%d,%d][%dx%d]", title, ((Component) component).getX(), ((Component)
      // component).getY(), ((Component) component)
      // .getWidth(), ((Component) component).getHeight());
      // return title;
    }

    // TODO identify other components' readable signature
    return "unknown(" + component.getClass().getName() + ")";
  }

  public static String getComponentClassName(Accessible component) {
    return component.getClass().getName();
  }

  public static String getIconName(Accessible component) {
    String iconName = null;
    Object result = IOUtil.invokeGetMethod(component, "getIcon");
    if (result == null)
      return null;
    result = IOUtil.invokeGetMethod(result, "getDescription");
    if (result == null)
      return null;
    iconName = result.toString();
    if (iconName != null) {
      int pivot = iconName.lastIndexOf("/");
      return iconName.substring(pivot + 1);
    }

    return iconName;
  }

  public static String getLayoutName(Accessible component) {
    LayoutManager layoutMgr = ((Container) component).getLayout();
    if (layoutMgr == null)
      return "null";
    String layoutName = layoutMgr.getClass().getName();
    return layoutName;
  }

  public static int getComponentIndex(Accessible component) {
    if (!(component instanceof Component))
      return 0;
    Container parentComponent = ((Component) component).getParent();
    if (parentComponent != null) {
      Component[] components = parentComponent.getComponents();
      int index = 0;
      for (Component comp : components) {
        if (comp != null && comp.equals(component)) {
          return index;
        }
        index++;
      }
    }
    return 0;
  }

  public static <T> String getListenerNames(Class<T> listenerType, Object value) {
    T[] listeners = (T[]) value;
    HashSet<String> listenerNames = new HashSet<String>();
    String result = "";

    for (T listener : listeners) {
      String name = listener.getClass().getName();
      listenerNames.add(name);
    }

    ArrayList<String> list = new ArrayList<String>(listenerNames);
    Collections.sort(list);
    for (String name : list)
      result += name + ";";

    return result;
  }

  public static List<Object> getAccessibleChilds(Accessible parent) {
    List<Object> result = new ArrayList<Object>();
    if (parent != null) {
      AccessibleContext aContext = parent.getAccessibleContext();
      if (aContext != null) {
        int count = aContext.getAccessibleChildrenCount();
        for (int i = 0; i < count; i++) {
          result.add(aContext.getAccessibleChild(i));
        }
      }
    }
    return result;
  }

  public static boolean isModalBlocked(Window window) {

    try {
      // use trick(using reflection) to get the blocked window info.
      Method isModalBlockedMethod;
      isModalBlockedMethod = Window.class.getDeclaredMethod("isModalBlocked", new Class[0]);
      isModalBlockedMethod.setAccessible(true);
      boolean result = (Boolean) isModalBlockedMethod.invoke(window, new Object[0]);
      return result;

    } catch (Exception e) {
      e.printStackTrace();
      return false;

    }

  }

  public static void expandAll(JTree tree) {
    int row = 0;
    while (row < tree.getRowCount()) {
      tree.expandRow(row);
      row++;
    }
  }

  public static void expandToLast(JTree tree) {
    // expand to the last leaf from the root
    DefaultMutableTreeNode root;
    root = (DefaultMutableTreeNode) tree.getModel().getRoot();
    tree.scrollPathToVisible(new TreePath(root.getLastLeaf().getPath()));
  }

  public static void collapseAll(JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 0) {
      tree.collapseRow(row);
      row--;
    }
  }

  public static void waitForEventIdle() {
    // the idea is from google testing blog.

    if (EventQueue.isDispatchThread())
      return;

    ObjRef probe = new ObjRef(false);

    // insert probe
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // insert prove again
    probe.setRef(false);
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // JXTable display properties
  // center column header text
  @SuppressWarnings("serial")
  public static JXTable createXTable() {
    JXTable table = new JXTable() {

      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel) {

          @Override
          public void updateUI() {
            super.updateUI();
            // need to do in updateUI to survive toggling of LAF
            if (getDefaultRenderer() instanceof JLabel) {
              ((JLabel) getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

            }
          }
        };
      }
    };
    return table;
  }

  public static GUIModel shallowTreeCopy(GUIModel model) {
    GUIModel newModel = (GUIModel) model.clone();
    int childCount = model.getChildCount();
    for (int i = 0; i < childCount; i++) {
      newModel.add(shallowTreeCopy((GUIModel) model.getChildAt(i)));
    }
    return newModel;
  }

  public static void saveFullScreenImage(String filename, String fileType) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    Dimension screenSize = toolkit.getScreenSize();
    Rectangle screenRect = new Rectangle(screenSize);
    try {
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRect);

      ImageIO.write(image, fileType, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    } catch (AWTException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    }
  }

  public static void saveWindowsImage(GUIModel guiModel, String filename, String fileType) {
    int childCount = guiModel.getChildCount();
    Rectangle screenRect = null;
    for (int i = 0; i < childCount; i++) {
      WindowModel windowModel = (WindowModel) guiModel.getChildAt(i).getUserObject();
      Window window = (Window) windowModel.getRef();
      if (window == null || !window.isShowing())
        continue;
      Point point = new Point(0, 0);
      SwingUtilities.convertPointToScreen(point, window);

      if (screenRect == null)
        screenRect = new Rectangle(point, window.getSize());
      else
        screenRect.add(new Rectangle(point, window.getSize()));

    }
    try {
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRect);
      ImageIO.write(image, fileType, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    } catch (AWTException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    }
  }

  public static void saveComponentImage(final Component component, final String filename, final String fileType) {
    if (!component.isShowing())
      return;
    // Point point = new Point(0, 0);
    // SwingUtilities.convertPointToScreen(point, component);
    // Rectangle screenRect = new Rectangle(point, component.getSize());
    //
    // try {
    // Robot robot = new Robot();
    // BufferedImage image = robot.createScreenCapture(screenRect);
    // ImageIO.write(image, fileType, new File(filename));
    // } catch (IOException e) {
    // e.printStackTrace();
    // TestLogger.log.error(e.getMessage());
    // } catch (AWTException e) {
    // e.printStackTrace();
    // TestLogger.log.error(e.getMessage());
    // }
    if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0)
      return;

    // code below is invoked by the event queue because of the concurrency problem.
    try {
      EventQueue.invokeAndWait(new Runnable() {
        @Override
        public void run() {

          BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
              BufferedImage.TYPE_INT_RGB);
          // switch off double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);

          component.paint(image.getGraphics());

          // switch on double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
          try {
            ImageIO.write(image, fileType, new File(filename));
          } catch (IOException e) {
            e.printStackTrace();
          }

        }

      });
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }

  }

  public static BufferedImage loadImage(String filename) {
    try {
      BufferedImage image = ImageIO.read(new File(filename));
      return image;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getComponentRole(Accessible component) {
    if (component == null)
      return "null";

    AccessibleContext aContext = component.getAccessibleContext();
    if (aContext == null) {
      return "null(noAccessibleContext)";
    }

    AccessibleRole aRole = aContext.getAccessibleRole();

    if (aRole == null) {
      return "null(noAccessibleRole)";
    }
    return aRole.toDisplayString(Locale.ENGLISH);
  }

  public static AccessibleContext getAccessibleContext(ComponentModel model) {
    if (model == null)
      return null;
    Accessible accessible = (Accessible) model.getRef();
    if (accessible == null)
      return null;
    AccessibleContext aContext = accessible.getAccessibleContext();
    if (aContext == null)
      return null;
    return aContext;
  }

  public static byte[] getImageBuffer(final Component component) {
    if (!component.isShowing())
      return new byte[0];
    if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0)
      return new byte[0];

    final Object[] result = new Object[1];
    result[0] = new byte[0];

    // code below is invoked by the event queue because of the concurrency problem.
    try {
      EventQueue.invokeAndWait(new Runnable() {
        @Override
        public void run() {

          BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
              BufferedImage.TYPE_INT_RGB);
          // switch off double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);

          component.paint(image.getGraphics());

          // switch on double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
          try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            result[0] = imageInByte;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      });
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }
    return (byte[]) result[0];

  }

}
