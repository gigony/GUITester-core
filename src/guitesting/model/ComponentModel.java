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

import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.model.event.EventModel;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.accessibility.Accessible;

import com.google.common.hash.HashCode;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ComponentModel implements Converter, Serializable {
  private static final long serialVersionUID = 1L;

  protected GUIModel modelNode = null;
  protected WindowModel windowModel = null;
  protected transient Reference<Object> ref = null;
  protected PropertyModel properties = new PropertyModel();
  protected transient List<EventModel> eventList = new ArrayList<EventModel>();

  public ComponentModel() {

    properties.put("title", "GUI");
    properties.put("id", "0123456789");

    properties.put("class", "unknown");
  }

  public ComponentModel(Object aComponent) {
    this.setRef(aComponent);
  }

  public void setRef(Object ref) {
    /*
     * XXX Changed to SoftReference from WeakReference since AccessibleContext object can be collected by garbage
     * collector in the case of JTree. It can be garbage-collected earlier if I use 'WeakReference'.
     */
    this.ref = new SoftReference<Object>(ref);
  }

  public Object getRef() {
    if (ref == null)
      return null;
    return ref.get();
  }

  public void put(String key, String value) {
    properties.put(key, value);
  }

  public String get(String key) {
    return properties.get(key);
  }

  public List<EventModel> getEventList() {
    return eventList;
  }

  public void setModelNode(GUIModel guiModel) {
    modelNode = guiModel;
  }

  public GUIModel getModelNode() {
    return modelNode;

  }

  public WindowModel getWindowModel() {
    return windowModel;
  }

  public void setWindowModel(WindowModel windowModel) {
    this.windowModel = windowModel;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public boolean canConvert(Class clazz) {
    return ComponentModel.class.equals(clazz) || WindowModel.class.equals(clazz);
  }

  public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
    ComponentModel component = (ComponentModel) obj;

    if (component.get("title") != null)
      writer.addAttribute("title", component.get("title"));
    Map<String, String> properties = component.getProperties();
    List<String> keyList = new ArrayList<String>(properties.keySet());
    Collections.sort(keyList);
    writer.startNode("attributes"); // <attributes>
    for (String key : keyList) {
      if (!key.equals("title")) {
        writer.startNode(key); // <(key)>
        writer.setValue(properties.get(key));
        writer.endNode(); // </(key)>
      }
    }
    writer.endNode(); // </attributes>
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    ComponentModel component = null;
    String nodeName = reader.getNodeName();
    String title = reader.getAttribute("title");
    if (nodeName.equals("window"))
      component = new WindowModel(null);
    else
      component = new ComponentModel(null);
    if (title != null)
      component.put("title", title);

    reader.moveDown(); // <attributes>
    while (reader.hasMoreChildren()) {
      reader.moveDown(); // <(key)>
      String key = reader.getNodeName();
      String value = reader.getValue();
      component.put(key, value); // </(key)>
      reader.moveUp();
    }
    reader.moveUp(); // </attributes>
    return component;

  }

  @Override
  public boolean equals(Object obj) {
    ComponentModel target = (ComponentModel) obj;
    if (target.get("id").equals(get("id")))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    return HashCode.fromString(get("id")).asInt();
  }

  @Override
  public String toString() {
    return get("title");
  }

}
