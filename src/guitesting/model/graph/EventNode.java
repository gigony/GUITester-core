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
package guitesting.model.graph;

import guitesting.model.ITestCaseElement;
import guitesting.model.event.EventModel;

import java.io.Serializable;

import com.google.common.hash.HashCode;

public class EventNode implements Serializable, ITestCaseElement {
  public static final int SYSTEM_INTERACTION_EVENT = 1;
  public static final int TERMINATION_EVENT = 2;
  public static final int UNRESTRICTED_FOCUS_EVENT = 3;
  public static final int RESTRICTED_FOCUS_EVENT = 4;

  private static final long serialVersionUID = 1L;
  public static final EventNode NullNode = new EventNode();
  private String windowName = "";
  private String componentName = "";
  private String eventTypeName = "";
  private String actionPropertyString = "";

  private HashCode windowHashCode;
  private HashCode componentHashCode;
  private int valueHashCode;
  private HashCode hashCode;

  private int eventType;

  private transient EventModel eventModel;

  private EventNode() {
    setWindowHashCode(HashCode.fromInt(0));
    setComponentHashCode(HashCode.fromInt(0));
    setHashCode(HashCode.fromInt(0));
  }

  public EventNode(EventModel eventModel, int eventType) {
    setWindowName(eventModel.getWindowModel().get("title"));
    setComponentName(eventModel.getComponentModel().get("title"));
    setEventTypeName(eventModel.getEventTypeName());
    setActionPropertyString(eventModel.getActionPropertyString());

    setWindowHashCode(HashCode.fromString(eventModel.getWindowModel().get("id")));
    setComponentHashCode(HashCode.fromString(eventModel.getComponentModel().get("id")));
    setValueHashCode(eventModel.getValueHash());
    setHashCode(eventModel.getHashCode());
    setEventType(eventType);
    setEventModel(eventModel);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EventNode))
      return false;
    EventNode targetNode = (EventNode) obj;

    if (!targetNode.getEventTypeName().equals(getEventTypeName()))
      return false;
    if (!targetNode.getWindowHashCode().equals(getWindowHashCode()))
      return false;
    if (!targetNode.getComponentHashCode().equals(getComponentHashCode()))
      return false;
    if (targetNode.getValueHashCode() != getValueHashCode())
      return false;
    if (!targetNode.getHashCode().equals(getHashCode()))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return getHashCode().asInt();
  }

  @Override
  public String toString() {
    return getName();
  }

  public String getName() {
    return String.format("%s@%s[%s,%s](%s)", getComponentName(), getWindowName(), getEventTypeName(),
        getEventTypeString(), getActionPropertyString());
  }

  public String getEventName() {
    return String.format("%s(%s)", getComponentName(), getEventTypeName());
  }

  private String getEventTypeString() {
    switch (getEventType()) {
    case SYSTEM_INTERACTION_EVENT:
      return "SYS";
    case TERMINATION_EVENT:
      return "TERM";
    case UNRESTRICTED_FOCUS_EVENT:
      return "UNRESTRICT";
    case RESTRICTED_FOCUS_EVENT:
      return "RESTRICT";
    }
    return "";
  }

  public String getSimpleName() {
    return String.format("%s(%s)", getComponentName(), hashCode());
  }

  public String getWindowName() {
    return windowName;
  }

  public void setWindowName(String windowName) {
    this.windowName = windowName;
  }

  public String getComponentName() {
    return componentName;
  }

  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  public String getEventTypeName() {
    return eventTypeName;
  }

  public void setEventTypeName(String eventTypeName) {
    this.eventTypeName = eventTypeName;
  }

  public String getActionPropertyString() {
    return actionPropertyString;
  }

  public void setActionPropertyString(String actionPropertyString) {
    this.actionPropertyString = actionPropertyString;
  }

  public HashCode getWindowHashCode() {
    return windowHashCode;
  }

  public void setWindowHashCode(HashCode windowHashCode) {
    this.windowHashCode = windowHashCode;
  }

  public HashCode getComponentHashCode() {
    return componentHashCode;
  }

  public void setComponentHashCode(HashCode componentHashCode) {
    this.componentHashCode = componentHashCode;
  }

  public int getValueHashCode() {
    return valueHashCode;
  }

  public void setValueHashCode(int valueHashCode) {
    this.valueHashCode = valueHashCode;
  }

  @Override
  public boolean isMatched(EventModel eventModel) {

    if (!getHashCode().equals(eventModel.getHashCode()))
      return false;

    if (!getEventTypeName().equals(eventModel.getEventTypeName())) {
      return false;
    }

    if (!getWindowName().equals(eventModel.getWindowModel().get("title")))
      return false;

    if (!getComponentName().equals(eventModel.getComponentModel().get("title")))
      return false;

    if (!getWindowHashCode().equals(HashCode.fromString(eventModel.getWindowModel().get("id"))))
      return false;

    if (!getComponentHashCode().equals(HashCode.fromString(eventModel.getComponentModel().get("id"))))
      return false;

    if (getValueHashCode() != eventModel.getValueHash())
      return false;

    return true;

  }

  public HashCode getHashCode() {
    return hashCode;
  }

  public void setHashCode(HashCode hashCode) {
    this.hashCode = hashCode;
  }

  public int getEventType() {
    return eventType;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public EventModel getEventModel() {
    return eventModel;
  }

  public void setEventModel(EventModel eventModel) {
    this.eventModel = eventModel;
  }

}
