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
package guitesting.model.event;

import guitesting.engine.EventManager;
import guitesting.engine.idgenerator.IDGenerator;
import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.model.ComponentModel;
import guitesting.model.EventListModel;
import guitesting.model.ITestCaseElement;
import guitesting.model.PropertyModel;
import guitesting.model.WindowModel;
import guitesting.ui.GUITester;
import guitesting.util.IOUtil;
import guitesting.util.TestLogger;

import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.hash.HashCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("event")
public abstract class EventModel implements ITestCaseElement, Converter, Transferable, Serializable {
  private static final long serialVersionUID = 1L;
  public static final EventModel NullEvent = new NullEvent(null);
  protected WindowModel windowModel = null;
  protected ComponentModel componentModel = null;

  protected boolean isContainer = false;
  // data store for name and other info.(as a temporal repository)
  protected PropertyModel propertyModel = new PropertyModel();
  protected List<EventModel> children = null;
  protected transient Object userObj = null;

  public EventModel(ComponentModel model) {
    this.setComponentModel(model);
    if (model != null) {
      // find window model
      windowModel = GUIModelExtractor.getWindowModel(model);
      if (windowModel == null)
        TestLogger.error("CHECK::window model is null");
    }
  }

  public void perform(final Object... args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {

        try {
          performImpl(args); // changed not to use thread
        } catch (Throwable e) {

          TestLogger.error("###################### Uncaught exception detected #####################");
          UncaughtExceptionHandler test = Thread.getDefaultUncaughtExceptionHandler();
          if (test != null)
            test.uncaughtException(null, e);
          else {
            TestLogger.error("###################### throw just runtime exception#####################");
          }
          throw new RuntimeException();
        }

      }
    });
  }

  public abstract void performImpl(Object... args);

  public String getActionPropertyString() {
    return "";
  }

  public ComponentModel getComponentModel() {
    return componentModel;
  }

  public void setComponentModel(ComponentModel model) {
    this.componentModel = model;
  }

  public Object getAccessibleComponent() {
    return componentModel.getRef();
  }

  public boolean isSupported() {
    return isSupportedBy(componentModel);
  }

  public String toString() {
    return String.format("%s@%s[%s](%s)", getComponentModel().get("title"), getWindowModel().get("title"),
        getEventTypeName(), getActionPropertyString());

  }

  public abstract boolean isSupportedBy(ComponentModel model);

  public abstract boolean stopChainingIfSupported();

  public abstract String getEventTypeName();

  public String getEventName() {
    return String.format("%s(%s)", componentModel != null ? componentModel.get("title") : "", getEventTypeName());
  }

  public void setContainer(boolean isContainer) {
    this.isContainer = isContainer;
  }

  public boolean isContainer() {
    return isContainer;
  }

  public void setChildren(List<EventModel> children) {
    this.children = children;
  }

  public List<EventModel> getChildren() {
    return children;
  }

  public void setWindowModel(WindowModel windowModel) {
    this.windowModel = windowModel;
  }

  public WindowModel getWindowModel() {
    return windowModel;
  }

  public PropertyModel getPropertyModel() {
    return propertyModel;
  }

  public void setPropertyModel(PropertyModel propertyModel) {
    this.propertyModel = propertyModel;
  }

  public Object getUserObj() {
    return userObj;
  }

  public void setUserObj(Object userObj) {
    this.userObj = userObj;
  }

  public EventModel crateCloneObj() {
    EventListModel list = new EventListModel();
    list.add(this);
    EventListModel resultList = (EventListModel) IOUtil.deepCloneObj(list);
    EventModel result = resultList.get(0);
    result.componentModel.setRef(this.componentModel.getRef());
    result.windowModel.setRef(this.windowModel.getRef());
    return result;
  }

  public HashCode getHashCode() {
    return GUITester.getInstance().getIdGenerator().getEventHash(this);
  }

  // --- implement equal-related functions ----

  @Override
  public int hashCode() {
    return GUITester.getInstance().getIdGenerator().getEventHash(this).asInt();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EventModel))
      return false;
    EventModel targetEvent = (EventModel) obj;

    IDGenerator idGenerator = GUITester.getInstance().getIdGenerator();
    return idGenerator.getEventHash(this).equals(idGenerator.getEventHash(targetEvent));
  }

  public int getValueHash(){
    return 0;
  }

  @Override
  public boolean isMatched(EventModel eventModel) {
    return this.equals(eventModel);
  }

  // --- implement Converter interface ----

  public boolean canConvert(Class clazz) {
    return false;
  }

  public final void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    EventModel modelEvent = (EventModel) value;
    WindowModel modelWindow = modelEvent.getWindowModel();
    ComponentModel componentModel = modelEvent.getComponentModel();
    writer.addAttribute("name", getEventTypeName());
    PropertyModel property = modelEvent.getPropertyModel();
    if (property.size() > 0) {
      writer.addAttribute("valueSize", "" + property.size());
      List<String> keyList = new ArrayList<String>(property.keySet());
      Collections.sort(keyList);
      writer.startNode("values"); // <values>
      for (String key : keyList) {
        writer.startNode(key); // <(key)>
        writer.setValue(property.get(key));
        writer.endNode(); // </(key)>
      }
      writer.endNode(); // </attributes>
    }
    writer.startNode("window");// <window>
    context.convertAnother(modelWindow);
    writer.endNode();// </window>

    writer.startNode("component"); // <component>
    context.convertAnother(componentModel);
    writer.endNode();// </component>
    writer.startNode("action"); // <action>
    marshalAttributes(modelEvent, writer);
    writer.endNode();// </action>
  }

  protected void marshalAttributes(EventModel modelEvent, HierarchicalStreamWriter writer) {

  }

  public final Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

    String eventName = reader.getAttribute("name");
    String valueSizeStr = reader.getAttribute("valueSize");
    EventModel event = (EventModel) createObject(eventName);
    if (valueSizeStr != null) {

      PropertyModel property = event.getPropertyModel();
      reader.moveDown(); // <values>
      while (reader.hasMoreChildren()) {
        reader.moveDown(); // <(key)>
        String key = reader.getNodeName();
        String value = reader.getValue();
        property.put(key, value); // </(key)>
        reader.moveUp();
      }
      reader.moveUp(); // </values>
    }

    reader.moveDown(); // <window>
    WindowModel wModel = (WindowModel) context.convertAnother(null, WindowModel.class);
    reader.moveUp(); // </window>

    reader.moveDown(); // <component>
    ComponentModel cModel = (ComponentModel) context.convertAnother(null, ComponentModel.class);
    reader.moveUp(); // </component>

    event.setWindowModel(wModel);
    event.setComponentModel(cModel);

    reader.moveDown(); // <action>
    unmarshalAttributes(event, reader);
    reader.moveUp(); // </action>

    return event;
  }

  protected void unmarshalAttributes(EventModel event, HierarchicalStreamReader reader) {
  }

  private EventModel createObject(String eventName) {
    try {
      Class<? extends EventModel> eventClass = EventManager.getInstance().getEventHandlerByName(eventName);
      if (eventClass != null) {
        Constructor<? extends EventModel> constructor = eventClass
            .getConstructor(new Class<?>[] { ComponentModel.class });
        EventModel event = constructor.newInstance(new Object[] { null });
        return event;
      }
    } catch (Throwable t) {
      TestLogger.log.error("ERR: Event creation error!", t);
    }
    return null;
  }

  // / --- implementation of Transferable ---

  public static DataFlavor[] flavors;
  static {
    try {
      flavors = new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType) };
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(flavors[0]);
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor))
      return this;
    throw new UnsupportedFlavorException(flavor);
  }

}
