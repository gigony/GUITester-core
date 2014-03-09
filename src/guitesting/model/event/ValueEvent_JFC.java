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

import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.util.JFCUtil;
import guitesting.util.TestProperty;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleValue;
import javax.swing.AbstractButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("event")
public class ValueEvent_JFC extends EventModel {
  protected Number chooseValue = null;

  public ValueEvent_JFC(ComponentModel model, Number number) {
    super(model);
    chooseValue = number;
  }

  public ValueEvent_JFC(ComponentModel model) {
    super(model);

    if (model != null) {
      Accessible accessibleObj = (Accessible) model.getRef();
      // TODO value should be handled on demand
      isContainer = true;

      Map<String, String> customValues = TestProperty.eventValueManager.getMatchedCustomValues(this);
      ArrayList<String> targetValues = new ArrayList<String>();
      if (!customValues.isEmpty()) {
        for (int i = 1;; i++) {
          String value = customValues.get("v" + i);
          if (value != null) {
            targetValues.add(value);
          } else
            break;
        }
      }
      if (targetValues.size() == 0) {
        // default values
        AccessibleValue aValue = JFCUtil.getAccessibleContext(componentModel).getAccessibleValue();

        int minNumber = Integer.MAX_VALUE;
        int maxNumber = Integer.MIN_VALUE;

        Number minObj = aValue.getMinimumAccessibleValue();
        if (minObj != null) {
          minNumber = aValue.getMinimumAccessibleValue().intValue();
        }

        if (aValue.getMaximumAccessibleValue() != null) {
          maxNumber = aValue.getMaximumAccessibleValue().intValue();
        }
        if (minNumber != Integer.MAX_VALUE && maxNumber != Integer.MIN_VALUE) {
          int midNumber = (minNumber + maxNumber) / 2;
          targetValues.add("" + minNumber);
          targetValues.add("" + midNumber);
          targetValues.add("" + maxNumber);
        } else {
          targetValues.add("1");
        }
      }

      children = new ArrayList<EventModel>(targetValues.size());
      int index = 1;
      for (String value : targetValues) {
        getPropertyModel().put("v" + index, value);
        children.add(new ValueEvent_JFC(model, Integer.valueOf(value)));
        index++;
      }

    }
  }

  @Override
  public void performImpl(Object... args) {
    if (isContainer)
      return;
    ((Window) getWindowModel().getRef()).requestFocus();

    // Move to front if JInternalFrame is used - Remove if this doesn't necessary
    GUIModel currentNode = getComponentModel().getModelNode();
    while (currentNode != null) {

      Object userObj = currentNode.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel componentModel = (ComponentModel) userObj;
        if (componentModel.get("class").endsWith("JInternalFrame")) {
          ((JInternalFrame) componentModel.getRef()).moveToFront();
          break;
        }
      }

      currentNode = (GUIModel) currentNode.getParent();
    }

    Accessible accessibleObj = (Accessible) getComponentModel().getRef();
    AccessibleValue aValue = JFCUtil.getAccessibleContext(componentModel).getAccessibleValue();
    aValue.setCurrentAccessibleValue(chooseValue);
    // if (accessibleObj instanceof JScrollBar) {
    // AccessibleValue aValue = getAccessibleContext().getAccessibleValue();
    // aValue.setCurrentAccessibleValue(chooseValue);
    // } else {
    // AccessibleValue aValue = getAccessibleContext().getAccessibleValue();
    // int randomNumber = new Random().nextInt(maxNumber - minNumber + 1) + minNumber;
    // aValue.setCurrentAccessibleValue(new Integer(randomNumber));
    // }
  }

  @Override
  public String getEventTypeName() {
    return "value event";
  }

  @Override
  public String getActionPropertyString() {
    return (chooseValue != null) ? chooseValue.toString() : "";
  }

  @Override
  public int getValueHash() {
    return (chooseValue != null) ? chooseValue.intValue() * 13 : 0;
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {
    AccessibleValue aValue = JFCUtil.getAccessibleContext(model).getAccessibleValue();
    if (aValue == null)
      return false;
    if (model.getRef() instanceof AbstractButton)
      return false;
    if (model.getRef() instanceof JMenuItem)
      return false;
    if (model.getRef() instanceof JInternalFrame)
      return false;

    return true;
  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

  @Override
  public boolean canConvert(Class clazz) {
    return ValueEvent_JFC.class.equals(clazz);
  }

  @Override
  protected void marshalAttributes(EventModel modelEvent, HierarchicalStreamWriter writer) {
    super.marshalAttributes(modelEvent, writer);
    if (((ValueEvent_JFC) modelEvent).chooseValue != null) {
      writer.startNode("chooseValue");
      writer.setValue("" + ((ValueEvent_JFC) modelEvent).chooseValue.intValue());
      writer.endNode();
    }

  }

  @Override
  protected void unmarshalAttributes(EventModel event, HierarchicalStreamReader reader) {
    super.unmarshalAttributes(event, reader);
    if (reader.hasMoreChildren()) {
      reader.moveDown();
      ((ValueEvent_JFC) event).chooseValue = Integer.valueOf(reader.getValue());
      reader.moveUp();
    }
  }

}
