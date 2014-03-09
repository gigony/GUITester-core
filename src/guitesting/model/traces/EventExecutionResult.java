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
package guitesting.model.traces;

import guitesting.model.event.EventModel;
import guitesting.ui.GUITester;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.accessibility.Accessible;
import javax.swing.SwingUtilities;

import com.google.common.hash.HashCode;

public class EventExecutionResult implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final int EVENT_SUCCEED = 1;
  public static final int EVENT_FAILED = 2;
  public static final int EVENT_NOT_FOUND = 3;

  private String windowName = "";
  private String componentName = "";
  private String eventTypeName = "";
  private String actionPropertyString = "";

  private HashCode windowHashCode;
  private HashCode componentHashCode;
  private int valueHashCode;
  private HashCode hashCode;

  private int globalIndex;
  private int testCaseIndex;
  private int localIndex;

  private long startTime;
  private long endTime;

  private Rectangle componentRect;

  private String[] guiStateHashingValue = { "", "", "" };

  public EventExecutionResult(EventModel eventModel, int globalIndex, int testCaseIndex, int localIndex,
      long startTime, long endTime) {
    setWindowName(eventModel.getWindowModel().get("title"));
    setComponentName(eventModel.getComponentModel().get("title"));
    setEventTypeName(eventModel.getEventTypeName());
    setActionPropertyString(eventModel.getActionPropertyString());

    setWindowHashCode(HashCode.fromString(eventModel.getWindowModel().get("id")));
    setComponentHashCode(HashCode.fromString(eventModel.getComponentModel().get("id")));
    setValueHashCode(eventModel.getValueHash());
    setHashCode(GUITester.getInstance().getIdGenerator().getEventHash(eventModel));

    this.setGlobalIndex(globalIndex);
    this.setTestCaseIndex(testCaseIndex);
    this.setLocalIndex(localIndex);

    this.setStartTime(startTime);
    this.setEndTime(endTime);

    Accessible accessibleComp = (Accessible) eventModel.getAccessibleComponent();
    if (accessibleComp != null) {
      if (accessibleComp instanceof Component) {
        Component window = (Component) eventModel.getWindowModel().getRef();
        if (window != null) {
          Component component = (Component) accessibleComp;
          Point startpos = SwingUtilities.convertPoint(component, new Point(0, 0), window);
          setComponentRect(new Rectangle(startpos, new Dimension(component.getWidth(), component.getHeight())));
        }
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EventExecutionResult))
      return false;
    EventExecutionResult targetNode = (EventExecutionResult) obj;

    if (!targetNode.getEventTypeName().equals(getEventTypeName()))
      return false;
    if (targetNode.getWindowHashCode() != getWindowHashCode())
      return false;
    if (targetNode.getComponentHashCode() != getComponentHashCode())
      return false;
    if (targetNode.getValueHashCode() != getValueHashCode())
      return false;
    if (targetNode.getHashCode() != getHashCode())
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
    return String.format("%s@%s[%s](%s)", getComponentName(), getWindowName(), getEventTypeName(),
        getActionPropertyString());
  }

  public String getSimpleName() {
    return String.format("%s(%d)", getComponentName(), getActionPropertyString().hashCode());
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

  public HashCode getHashCode() {
    return hashCode;
  }

  public void setHashCode(HashCode hashCode) {
    this.hashCode = hashCode;
  }

  public int getGlobalIndex() {
    return globalIndex;
  }

  public void setGlobalIndex(int globalIndex) {
    this.globalIndex = globalIndex;
  }

  public int getTestCaseIndex() {
    return testCaseIndex;
  }

  public void setTestCaseIndex(int testCaseIndex) {
    this.testCaseIndex = testCaseIndex;
  }

  public int getLocalIndex() {
    return localIndex;
  }

  public void setLocalIndex(int localIndex) {
    this.localIndex = localIndex;
  }

  public Rectangle getComponentRect() {
    return componentRect;
  }

  public void setComponentRect(Rectangle componentRect) {
    this.componentRect = componentRect;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public String getGuiStateHashingValue() {
    return guiStateHashingValue[0];
  }

  public String getGuiStateHashingValue(int index) {
    return guiStateHashingValue[index];
  }

  public void setGuiStateHashingValue(String guiStateHashingValue) {
    this.guiStateHashingValue[0] = guiStateHashingValue;
  }

  public void setGuiStateHashingValue(String[] guiStateHashingValue) {
    this.guiStateHashingValue = guiStateHashingValue;
  }

  public void setGuiStateHashingValue(int index, String guiStateHashingValue) {
    this.guiStateHashingValue[index] = guiStateHashingValue;
  }
}
