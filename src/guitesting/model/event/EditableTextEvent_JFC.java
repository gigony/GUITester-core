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
import guitesting.model.WindowModel;
import guitesting.ui.GUITester;
import guitesting.util.JFCUtil;
import guitesting.util.TestProperty;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleEditableText;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("event")
public class EditableTextEvent_JFC extends EventModel {
  protected String chooseText = "";

  public EditableTextEvent_JFC(ComponentModel model, String string) {
    super(model);
    chooseText = string;
  }

  public EditableTextEvent_JFC(ComponentModel model) {
    super(model);
    if (model != null) {
      Accessible accessibleObj = (Accessible) model.getRef();

      if (accessibleObj instanceof JTextComponent) {
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
          targetValues
              .add("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`1234567890-=,./\\;'~!@#$%^&*()_+|:<>?가나다라마바사아자차카타파하");
          targetValues.add("1");
        }
        children = new ArrayList<EventModel>(targetValues.size());
        int index = 1;
        for (String value : targetValues) {
          getPropertyModel().put("v" + index, value);
          children.add(new EditableTextEvent_JFC(model, value));
          index++;
        }

      }
    }
  }

  @Override
  public void performImpl(Object... args) {
    if (isContainer)
      return;
    ((Window) getWindowModel().getRef()).requestFocus();
    AccessibleEditableText aEditableText = JFCUtil.getAccessibleContext(componentModel).getAccessibleEditableText();
    // check if it contains special string
    int index = chooseText.indexOf("{%workspace_folder}");
    String inputText = chooseText;
    if (index >= 0) {
      inputText = chooseText.substring(0, index) + TestProperty.getFolder("workspace").getAbsolutePath()
          + chooseText.substring(index + "{%workspace_folder}".length());
    }
    boolean doActionPerformed = false;
    if (inputText.endsWith("@\"doActionPerformed\"")) {
      inputText = inputText.substring(0, inputText.indexOf("@\"doActionPerformed\""));
      doActionPerformed = true;
    }

    try {
      aEditableText.setTextContents(inputText);
    } catch (Exception e) {
      try {
        Accessible accessibleobj = (Accessible) getAccessibleComponent();
        if (accessibleobj instanceof Component) {
          Component component = (Component) accessibleobj;
          Method setText = component.getClass().getMethod("setText", String.class);
          setText.invoke(component, inputText);
        }

      } catch (Exception e1) {
      }
    }
    if (doActionPerformed) {
      Accessible accessibleobj = (Accessible) getAccessibleComponent();
      if (accessibleobj instanceof JTextField) {
        ((JTextField) accessibleobj).requestFocus();
        ((JTextField) accessibleobj).postActionEvent();
      }
    }

  }

  @Override
  public String getEventTypeName() {
    return "text event";
  }

  @Override
  public String getActionPropertyString() {
    return chooseText;
  }

  @Override
  public int getValueHash() {
    return Hashing.crc32().hashString(chooseText, Charsets.UTF_8).asInt();
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {
    // String isShowing = model.get("visible");
    // if (!"true".equals(isShowing))
    // return false;
    // isShowing = model.get("enabled");
    // if (!"true".equals(isShowing))
    // return false;

    // skip file chooser dialog
    WindowModel winModel = GUITester.getInstance().getGuiModelExtractor().getWindowModel(model);
    if (winModel != null && winModel.get("windowlisteners") != null
        && winModel.get("windowlisteners").indexOf("javax.swing.JFileChooser") >= 0) {
      return false;
    }

    AccessibleEditableText aValue = JFCUtil.getAccessibleContext(model).getAccessibleEditableText();
    if (aValue == null)
      return false;

    Accessible accessibleObj = (Accessible) model.getRef();
    if (accessibleObj instanceof JTextComponent) {
      if (!((JTextComponent) accessibleObj).isEditable())
        return false;
    }

    return true;
  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

  // --- implement Converter interface ---

  @Override
  public boolean canConvert(Class clazz) {
    return EditableTextEvent_JFC.class.equals(clazz);
  }

  @Override
  protected void marshalAttributes(EventModel modelEvent, HierarchicalStreamWriter writer) {
    super.marshalAttributes(modelEvent, writer);
    if (((EditableTextEvent_JFC) modelEvent).chooseText != null) {
      writer.startNode("chooseText");
      writer.setValue("" + ((EditableTextEvent_JFC) modelEvent).chooseText);
      writer.endNode();
    }

  }

  @Override
  protected void unmarshalAttributes(EventModel event, HierarchicalStreamReader reader) {
    super.unmarshalAttributes(event, reader);
    if (reader.hasMoreChildren()) {
      reader.moveDown();
      ((EditableTextEvent_JFC) event).chooseText = reader.getValue();
      reader.moveUp();
    }
  }

}
