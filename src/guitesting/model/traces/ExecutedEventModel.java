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

import java.io.Serializable;

import com.google.common.hash.HashCode;

public class ExecutedEventModel implements Serializable, Comparable<ExecutedEventModel> {
  private static final long serialVersionUID = 1L;

  int localCount;
  int globalCount;

  String name = "";
  String windowName = "";
  String eventTypeName = "";
  String actionPropertyString = "";

  int valueHashCode;
  HashCode componentHashCode;
  HashCode windowHashCode;
  HashCode hashCode;
  // Rectangle componentRect;

  public final static ExecutedEventModel NULLExecutedEvent = new ExecutedEventModel(null, 0, 0);

  public ExecutedEventModel(EventModel nextEvent, int localCnt, int globalCnt) {
    if (nextEvent == null)
      return;

    name = nextEvent.getComponentModel().get("title");
    windowName = nextEvent.getWindowModel().get("title");
    eventTypeName = nextEvent.getEventTypeName();
    actionPropertyString = nextEvent.getActionPropertyString();
    valueHashCode = nextEvent.getValueHash();
    componentHashCode = HashCode.fromString(nextEvent.getComponentModel().get("id"));
    windowHashCode = HashCode.fromString(nextEvent.getWindowModel().get("id"));

    hashCode = GUITester.getInstance().getIdGenerator().getEventHash(nextEvent);

    // Accessible accessibleComp = (Accessible) nextEvent.getComponentModel().getRef();
    // if (accessibleComp instanceof Component) {
    // Component window = (Component) nextEvent.getWindowModel().getRef();
    // Component component = (Component) accessibleComp;
    // Point startpos = SwingUtilities.convertPoint(component, new Point(0, 0), window);
    // componentRect = new Rectangle(startpos, new Dimension(component.getWidth(), component.getHeight()));
    // }

    this.localCount = localCnt;
    this.globalCount = globalCnt;

  }

  private ExecutedEventModel() {

  }

  public static ExecutedEventModel createNullEventModel() {
    return new ExecutedEventModel();
  }

  public HashCode getHashCode() {
    return hashCode;
  }

  public String getName() {
    return name;
  }

  public String getWindowName() {
    return windowName;
  }

  public String getEventTypeName() {
    return eventTypeName;
  }

  public String getActionPropertyString() {
    return actionPropertyString;
  }

  public int getValueHashCode() {
    return valueHashCode;
  }

  public HashCode getComponentHashCode() {
    return componentHashCode;
  }

  public HashCode getWindowHashCode() {
    return windowHashCode;
  }

  public int getLocalCount() {
    return localCount;
  }

  public int getGlobalCount() {
    return globalCount;
  }

  // public Rectangle getComponentRect() {
  // return componentRect;
  // }

  @Override
  public int hashCode() {
    return getHashCode().asInt();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ExecutedEventModel) {
      ExecutedEventModel targetObj = (ExecutedEventModel) obj;
      return getHashCode().equals(targetObj.getHashCode());
    }
    return getHashCode().equals(obj.hashCode());
  }

  @Override
  public int compareTo(ExecutedEventModel param) {
    if (hashCode() < param.hashCode())
      return -1;
    if (hashCode() > param.hashCode())
      return 1;
    return 0;
  }

  @Override
  public String toString() {
    return getName();
  }
}
