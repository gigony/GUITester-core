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
package guitesting.model;

import guitesting.model.event.EventModel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

// 2011-06-12 addeed group number in getEvents(GUIModel) 
@XStreamAlias("gui")
public class GUIModel extends DefaultMutableTreeNode implements Converter, TreeTableNode {

  public GUIModel(boolean isRoot) {
    if (isRoot)
      setUserObject(new ComponentModel());
  }

  public GUIModel(Object userObject) {
    super(userObject);
    if (userObject instanceof ComponentModel)
      ((ComponentModel) userObject).setModelNode(this);
  }

  public void dump(PrintStream out) {
    printTree(out, 0);
    out.println("----------------");

  }

  private void printTree(PrintStream out, int depth) {
    ComponentModel element = (ComponentModel) getUserObject();
    out.print(depth + ": ");
    for (int i = 0; i < depth; i++)
      out.print("  ");

    String className = element.get("class");
    className = className == null ? "" : className.substring(className.lastIndexOf(".") + 1);
    out.println(String.format("%s(%s)", className, element.get("title")));

    if (getChildCount() > 0) {
      GUIModel child = (GUIModel) getFirstChild();
      while (child != null) {
        child.printTree(out, depth + 1);
        child = (GUIModel) child.getNextSibling();
      }
    }

  }

  public List<EventModel> getEvents() {
    return getEvents(this, false);
  }

  public List<EventModel> getEvents(boolean includeDisabledWidget) {
    return getEvents(this, includeDisabledWidget);
  }

  @SuppressWarnings("unchecked")
  public static LinkedList<EventModel> getEvents(GUIModel rootNode) {
    return getEvents(rootNode, false);
  }

  public ArrayList<WindowModel> getActiveWindowList() {
    return getActiveWindowList(this);
  }
  
  public ArrayList<WindowModel> getWindowList() {
    return getWindowList(this,true);
  }

  public static ArrayList<WindowModel> getActiveWindowList(GUIModel rootNode) {
    return getWindowList(rootNode, false);
  }

  public static ArrayList<WindowModel> getWindowList(GUIModel rootNode, boolean includeBlockedWindow) {
    ArrayList<WindowModel> result = new ArrayList<WindowModel>();

    Enumeration<GUIModel> children = (Enumeration<GUIModel>) rootNode.children();
    while (children.hasMoreElements()) {
      GUIModel child = children.nextElement();
      Object userObj = child.getUserObject();
      if (userObj instanceof WindowModel) {
        WindowModel winModel = (WindowModel) userObj;

        if ((includeBlockedWindow || !"true".equals(winModel.get("modalblocked")))
            && "true".equals(winModel.get("displayable")) && "true".equals(winModel.get("visible")))
          result.add(winModel);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static LinkedList<EventModel> getEvents(GUIModel rootNode, boolean includeDisabledWidget) {
    LinkedList<EventModel> result = new LinkedList<EventModel>();
    HashSet<EventModel> temp = new HashSet<EventModel>();
    // get event node

    for (Enumeration<GUIModel> enumurator = rootNode.depthFirstEnumeration(); enumurator.hasMoreElements();) {
      GUIModel node = enumurator.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel element = (ComponentModel) userObj;

        boolean isEnabled = !"false".equals(element.get("enabled"));
        boolean isVisible = !"false".equals(element.get("visible"));
        if (includeDisabledWidget || (isVisible && isEnabled)) {
          List<EventModel> eventList = element.getEventList();
          result.addAll(eventList);
        }
      }
    }
    // expand an event node if the node is container event node.
    for (int i = 0; i < result.size();) {
      EventModel modelEvent = result.get(i);
      if (modelEvent.isContainer()) {
        result.remove(i);

        List<EventModel> eventList = modelEvent.getChildren();
        for (EventModel e : eventList) {
          if (!temp.contains(e)) {
            result.add(e);
            temp.add(e);
          }
        }
      } else
        i++;
    }
    return result;
  }

  public String toString() {
    if (getUserObject() instanceof ComponentModel)
      return ((ComponentModel) getUserObject()).get("title");
    if (getUserObject() instanceof EventModel)
      return ((EventModel) getUserObject()).toString();
    return "unknown";
  }

  // --- implements Converter interface---

  public boolean canConvert(Class clazz) {
    return GUIModel.class.equals(clazz);
  }

  public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    GUIModel model = (GUIModel) value;

    int childCount = model.getChildCount();
    for (int i = 0; i < childCount; i++) {
      traverseChild((GUIModel) model.getChildAt(i), writer, context);
    }
  }

  private void traverseChild(GUIModel model, HierarchicalStreamWriter writer, MarshallingContext context) {
    Object userObj = model.getUserObject();
    if (userObj instanceof ComponentModel) {
      if (userObj instanceof WindowModel)
        writer.startNode("window"); // <window>
      else
        writer.startNode("component"); // <component>

      writer.addAttribute("childCount", "" + model.getChildCount());

      context.convertAnother(userObj);

      writer.startNode("contents"); // <content>
      int childCount = model.getChildCount();
      for (int i = 0; i < childCount; i++) {
        traverseChild((GUIModel) model.getChildAt(i), writer, context);
      }
      writer.endNode(); // </content>

      writer.endNode(); // </window> or </component>
    }
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    GUIModel model = new GUIModel(true);

    while (reader.hasMoreChildren()) {
      reader.moveDown(); // <window> or <component>
      traverseChild(model, reader, context);
      reader.moveUp(); // </window> or </component>
    }

    return model;
  }

  private void traverseChild(GUIModel parentModel, HierarchicalStreamReader reader, UnmarshallingContext context) {
    int childCount = Integer.valueOf(reader.getAttribute("childCount"));
    ComponentModel component = (ComponentModel) context.convertAnother(null, ComponentModel.class);
    GUIModel model = new GUIModel(component);
    component.setModelNode(model);
    parentModel.add(model);
    model.setParent(parentModel);
    if (childCount > 0) {
      reader.moveDown(); // <contents>;
      while (reader.hasMoreChildren()) {
        reader.moveDown(); // <window> or <component>
        traverseChild(model, reader, context);
        reader.moveUp(); // </window> or </component>
      }
      reader.moveUp(); // </contents>
    }
  }

  // --- implements TreeTableNode interface---

  public int getColumnCount() {
    return 3;
  }

  public Object getValueAt(int column) {
    Object obj = getUserObject();
    switch (column) {
    case 0:
      return obj;
    case 1:
      if (obj instanceof ComponentModel) {
        ComponentModel comp = (ComponentModel) obj;
        return String.format("(%s,%s)", comp.get("x"), comp.get("y"));
      }
      return obj.toString();
    case 2:
      if (obj instanceof ComponentModel) {
        ComponentModel comp = (ComponentModel) obj;
        return String.format("(%s,%s)", comp.get("width"), comp.get("height"));
      }
      return obj.toString();
    }
    return obj;
  }

  public boolean isEditable(int column) {
    return false;
  }

  public void setValueAt(Object aValue, int column) {
  }

  @Override
  public TreeTableNode getParent() {
    return (TreeTableNode) super.getParent();
  }

  @Override
  public TreeTableNode getChildAt(int index) {
    return (TreeTableNode) super.getChildAt(index);
  }

}
