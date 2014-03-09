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

import guitesting.engine.EventManager;
import guitesting.engine.idgenerator.IDGenerator;
import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.model.WindowModel;
import guitesting.model.event.EventModel;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;

import com.google.common.hash.HashCode;

/* Changes:
 *  2010-08-22: Get GUI Model for all windows(don't add event nodes when a window is blocked)
 */

public abstract class GUIModelExtractor {
  protected JFrame testerFrame = null; // this is used to ignore GUITester's frame while extracting windows

  public void setTesterFrame(JFrame ui) {
    testerFrame = ui;
  }

  public GUIModel getGUIModel() {
    return getGUIModel(true);
  }

  public abstract GUIModel getGUIModel(boolean includeDisabledWidget);

  public void extractComponentModel(GUIModel rootNode, Object aComponent, boolean isModalBlocked, boolean isVisible,
      boolean includeDisabledWidget, HashCode hashCode) {
    GUIModel childNode;
    ComponentModel model = createModel(aComponent);
    childNode = new GUIModel((Object) model);
    rootNode.add(childNode);

    model.setWindowModel(getWindowModel(model));

    // extract info.
    HashCode modelHashCode = extractProperties(model, hashCode); // extract properties

    if (isWindow(aComponent))
      hashCode = modelHashCode; // propagate only window's hash code to children.

    boolean isEnabled = !"false".equals(model.get("enabled"));

    if (model.get("visible") != null && model.get("visible").equals("false")) // 2011-11-21 add isVisible property
      isVisible = false;

    model.put("visible", String.format("%s", isVisible)); // set actual visibility( this value is similar to
                                                          // 'isShowing')

    if (!isModalBlocked && (includeDisabledWidget || (isEnabled && isVisible))) // 2011-11-21 add isVisible property
      GUIModelExtractor.extractEvents(model); // extract events

    // do not traverse if current component should be filtered
    if (isIgnorableModel(model) || TestProperty.eventFilter.shouldFilterComponent(model))
      return;

    // do not extract child components
    if (!isModalBlocked && (includeDisabledWidget || (isEnabled && isVisible))) {

      // traverse accessible children
      List<Object> childComponents = getAccessibleChilds(model);
      for (Object component : childComponents) {
        extractComponentModel(childNode, component, isModalBlocked, isVisible, includeDisabledWidget, hashCode);
      }
    }

  }

  public abstract List<Object> getAccessibleChilds(ComponentModel model);

  public boolean isIgnorableModel(ComponentModel model) {
    return false;
  }

  public abstract boolean isWindow(Object aComponent);

  public ComponentModel createModel(Object aComponent) {
    ComponentModel result = null;
    if (isWindow(aComponent))
      result = new WindowModel(aComponent);
    else
      result = new ComponentModel(aComponent);
    return result;
  }

  public static WindowModel getWindowModel(ComponentModel model) {
    if (model instanceof WindowModel)
      return (WindowModel) model;

    GUIModel modelNode = (GUIModel) model.getModelNode().getParent();
    while (modelNode != null) {
      Object userObj = modelNode.getUserObject();
      if (userObj instanceof WindowModel) {
        return (WindowModel) userObj;
      }
      modelNode = (GUIModel) modelNode.getParent();
    }

    return null;
  }

  public static void extractEvents(ComponentModel model) {

    // filter component if the property of component and it's window is matched
    if (TestProperty.eventFilter.shouldFilterComponent(model))
      return;
    List<EventModel> eventList = model.getEventList();
    // check eventHandler whether the event can be handled one by one in the list
    for (Class<? extends EventModel> handlerClass : EventManager.getInstance().getEventHandlerClassesList()) {
      try {
        EventModel eventChecker = EventManager.getInstance().getEventFromPool(handlerClass);
        if (eventChecker.isSupportedBy(model)) {
          Constructor<? extends EventModel> constructor = handlerClass
              .getConstructor(new Class<?>[] { ComponentModel.class });
          EventModel event = constructor.newInstance(new Object[] { model });
          eventList.add(event);

          // break if need to stop chaining
          if (eventChecker.stopChainingIfSupported()) // if need to stop chaining
            break;
        }
      } catch (Throwable t) {
        TestLogger.log.error("ERR: Event creation error!", t);
      }
    }
  }

  public abstract HashCode extractProperties(ComponentModel componentModel, HashCode hashCode);

  /**
   * create a tree which event nodes are added. 'root' may be modified.
   * 
   * @param root
   */
  public static void addEventNode(GUIModel root) {
    // GUIModel root = (GUIModel) treeModel.getRoot();

    for (Enumeration<GUIModel> enumuration = (Enumeration<GUIModel>) root.depthFirstEnumeration(); enumuration
        .hasMoreElements();) {
      GUIModel node = enumuration.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel component = (ComponentModel) userObj;
        List<EventModel> eventList = component.getEventList();
        for (EventModel event : eventList) {
          GUIModel childNode = new GUIModel(event);
          node.add(childNode);
          if (event.isContainer()) {
            List<EventModel> childrenEventList = event.getChildren();
            for (EventModel childEvent : childrenEventList) {
              GUIModel childEventNode = new GUIModel(childEvent);
              childNode.add(childEventNode);
            }
          }
        }
      }

    }
  }

  

  public abstract WindowModel extractWindowInfo(Object window);

  public abstract boolean isAvailable(WindowModel windowModel);

  public abstract boolean isModalBlocked(WindowModel windowModel);

  public abstract byte[] getImageBuffer(WindowModel element);
  public abstract IDGenerator getDefaultIDGenerator();

  public abstract List<String> getPropertyList();
}
