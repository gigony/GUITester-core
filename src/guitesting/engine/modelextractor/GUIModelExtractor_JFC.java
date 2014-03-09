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
package guitesting.engine.modelextractor;

import guitesting.engine.idgenerator.IDGenerator;
import guitesting.engine.idgenerator.IDGenerator_JFC;
import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.model.WindowModel;
import guitesting.ui.GUITester;
import guitesting.util.JFCUtil;
import guitesting.util.TestProperty;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.accessibility.Accessible;
import javax.swing.event.AncestorListener;

import com.google.common.hash.HashCode;

public class GUIModelExtractor_JFC extends GUIModelExtractor {
  private static List<String> propertyList = Arrays.asList("title", "id", "class", "componentIndex", "name",
      "actioncommand", "role", "actionlisteners", "ancestorlisteners", "windowlisteners", "componentlisteners",
      "containerlisteners", "focuslisteners", "hierarchyboundslisteners", "hierarchylisteners", "inputmethodlisteners",
      "keylisteners", "mouselisteners", "mousemotionlisteners", "mousewheellisteners", "propertychangelisteners",
      "vetoablechangelisteners", "modalblocked", "layout", "x", "y", "width", "height", "opaque", "enabled",
      "selected", "displayable", "editable", "visible", "focusable", "focustraversable", "showing", "cursorset",
      "componentcount", "alignmentx", "alignmenty", "backgroundset", "foregroundset", "background", "foreground",
      "font", "fontset", "text", "invokercomponent", "tooltiptext");
  // private static List<String> GUIPropertyGetterList = Arrays.asList(
  // "<java.awt.event.ActionEvent: java.lang.String getActionCommand()>", "<java.awt.Component: boolean isEnabled()>");

  private static Set<String> propertySet = new HashSet<String>(propertyList);

  // private static Set<String> GUIPropertyGetterSet = new HashSet<String>(GUIPropertyGetterList);

  @Override
  public GUIModel getGUIModel(boolean includeDisabledWidget) {
    GUIModel rootNode = new GUIModel(true);
    System.gc();

    Window[] allWindows = Window.getWindows();
    for (Window window : allWindows) {

      if (!window.equals(testerFrame)
          && (window.getClass().getName().startsWith("guitesting.examples.") || !window.getClass().getName()
              .startsWith("guitesting."))) // ignore
      // GUITester window and objects from packages starting from 'guitesting'
      {
        // don't get the information if the window is not displayable
        if (window.isDisplayable() && window.isVisible())
          extractComponentModel(rootNode, window, JFCUtil.isModalBlocked(window), true, includeDisabledWidget,
              HashCode.fromInt(0));
      }
    }

    return rootNode;
  }

  @Override
  public List<Object> getAccessibleChilds(ComponentModel model) {
    return JFCUtil.getAccessibleChilds((Accessible) model.getRef());
  }

  @Override
  public boolean isAvailable(WindowModel windowModel) {
    // need to update the information to retrieve the modal blocker information.
    Window[] allWindows = Window.getWindows();
    for (Window w : allWindows) {
      if (w.isDisplayable() && w.isVisible()) {
        WindowModel model = (WindowModel) createModel(w);
        model.setWindowModel(getWindowModel(model));
        extractProperties(model, HashCode.fromInt(0)); // extract properties

        if (windowModel.get("id").equals(model.get("id"))) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isModalBlocked(WindowModel windowModel) {
    // need to update the information to retrieve the modal blocker information.
    Window[] allWindows = Window.getWindows();
    for (Window w : allWindows) {
      if (w.isDisplayable() && w.isVisible()) {
        WindowModel model = (WindowModel) createModel(w);
        model.setWindowModel(GUIModelExtractor.getWindowModel(model));
        extractProperties(model, HashCode.fromInt(0)); // extract properties

        if (windowModel.get("id").equals(model.get("id"))) {
          return "true".equals(model.get("modalblocked"));
        }
      }
    }
    return false;
  }

  @Override
  public byte[] getImageBuffer(WindowModel element) {
    return JFCUtil.getImageBuffer((Component) element.getRef());
  }

  @Override
  public WindowModel extractWindowInfo(Object win) {
    Window window = (Window) win;

    boolean isVisible = false;
    if (window.isDisplayable() && window.isVisible())
      isVisible = true;
    boolean isModalBlocked = JFCUtil.isModalBlocked(window);
    WindowModel winModel = new WindowModel(window);
    extractProperties(winModel, HashCode.fromInt(0)); // extract properties

    if (!isModalBlocked && isVisible) // 2011-11-21 add isVisible property
      GUIModelExtractor.extractEvents(winModel); // extract events

    return winModel;
  }

  @Override
  public HashCode extractProperties(ComponentModel componentModel, HashCode hashCode) {
    Accessible aComponent = (Accessible) componentModel.getRef();
    Map<String, String> properties = componentModel.getProperties();

    Method[] methods = aComponent.getClass().getMethods();

    for (Method method : methods) {
      // get property name
      String propertyName = method.getName().toLowerCase(); // to lowercase
      if (propertyName.startsWith("is"))
        propertyName = propertyName.substring(2);
      else if (propertyName.startsWith("get"))
        propertyName = propertyName.substring(3);
      else
        continue;

      // save property if property name is in predefined property name set
      if (!propertySet.contains(propertyName))
        continue;

      int paramLength = method.getParameterTypes().length;
      if (propertyName.equals("actionlisteners") && method.getReturnType() != ActionListener[].class || paramLength > 0)
        continue;

      Object[] argments = new Object[0];

      try {
        Object rtnObj = method.invoke(aComponent, argments);

        String rtnString = convertToString(aComponent, propertyName, rtnObj); // converts to string
        properties.put(propertyName, rtnString);

      } catch (Exception e) {
      }
    }
    // insert 'title' property explicitly
    properties.put("title", JFCUtil.getComponentTitle(aComponent));
    properties.put("role", JFCUtil.getComponentRole(aComponent));
    // insert 'componentIndex' property
    properties.put("componentIndex", String.valueOf(JFCUtil.getComponentIndex(aComponent)));
    // explicitly
    if (componentModel instanceof WindowModel) {
      // insert 'modalblocked' property explicitly
      properties.put("modalblocked", "" + JFCUtil.isModalBlocked((Window) aComponent));
    }

    // replace strings
    TestProperty.eventFilter.replaceComponentString(componentModel);

    hashCode = GUITester.getInstance().getIdGenerator().getComponentHash(componentModel, hashCode);
    properties.put("id", "" + hashCode.toString()); // insert 'id' property explicitly

    return hashCode;
  }

  public String convertToString(Object comp, String key, Object value) {
    Accessible component = (Accessible) comp;
    if (value == null)
      return "null";

    if ("actionlisteners".equals(key))
      return JFCUtil.getListenerNames(ActionListener.class, value);
    else if ("ancestorlisteners".equals(key))
      return JFCUtil.getListenerNames(AncestorListener.class, value);
    else if ("componentlisteners".equals(key))
      return JFCUtil.getListenerNames(ComponentListener.class, value);
    else if ("windowlisteners".equals(key))
      return JFCUtil.getListenerNames(WindowListener.class, value);
    else if ("containerlisteners".equals(key))
      return JFCUtil.getListenerNames(ContainerListener.class, value);
    else if ("focuslisteners".equals(key))
      return JFCUtil.getListenerNames(FocusListener.class, value);
    else if ("hierarchyboundslisteners".equals(key))
      return JFCUtil.getListenerNames(HierarchyBoundsListener.class, value);
    else if ("hierarchylisteners".equals(key))
      return JFCUtil.getListenerNames(HierarchyListener.class, value);
    else if ("inputmethodlisteners".equals(key))
      return JFCUtil.getListenerNames(InputMethodListener.class, value);
    else if ("keylisteners".equals(key))
      return JFCUtil.getListenerNames(KeyListener.class, value);
    else if ("mouselisteners".equals(key))
      return JFCUtil.getListenerNames(MouseListener.class, value);
    else if ("mousemotionlisteners".equals(key))
      return JFCUtil.getListenerNames(MouseMotionListener.class, value);
    else if ("mousewheellisteners".equals(key))
      return JFCUtil.getListenerNames(MouseWheelListener.class, value);
    else if ("propertychangelisteners".equals(key))
      return JFCUtil.getListenerNames(PropertyChangeListener.class, value);
    else if ("vetoablechangelisteners".equals(key))
      return JFCUtil.getListenerNames(VetoableChangeListener.class, value);
    else if ("class".equals(key))
      return JFCUtil.getComponentClassName(component);
    else if ("icon".equals(key))
      return JFCUtil.getIconName(component);
    else if ("layout".equals(key))
      return JFCUtil.getLayoutName(component);

    return value.toString();
  }

  @Override
  public boolean isWindow(Object aComponent) {
    return aComponent instanceof Window;
  }

  @Override
  public boolean isIgnorableModel(ComponentModel model) {
    // For the performance issue(on TerpSpreadSheet5)
    if (model.get("class") != null && model.get("class").equals("javax.swing.JTable"))
      return true;
    return false;
  }

  @Override
  public IDGenerator getDefaultIDGenerator() {
    return new IDGenerator_JFC();
  }

  @Override
  public List<String> getPropertyList() {
    return propertyList;
  }

}
