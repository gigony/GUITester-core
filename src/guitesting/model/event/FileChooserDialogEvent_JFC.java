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
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleSelection;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.JTextComponent;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("event")
public class FileChooserDialogEvent_JFC extends EventModel {
  String fileName = "";
  int fileType = 0;
  String targetBtnName = "OK";

  public FileChooserDialogEvent_JFC(ComponentModel model, String fileName, int fileType, String targetBtnName) {
    super(model);
    this.fileName = fileName;
    this.fileType = fileType;
    this.targetBtnName = targetBtnName;
  }

  public FileChooserDialogEvent_JFC(ComponentModel model) {
    super(model);
    if (model != null) {
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
        targetValues.add("hello1234567890!@#$%^&**()_+-=\\/?<>,.`~|-1|OK");
      }
      children = new ArrayList<EventModel>(targetValues.size());
      int index = 1;
      for (String value : targetValues) {
        String[] tokens = value.split("\\|");
        String fileName = tokens[0];
        int fileType = Integer.valueOf(tokens[1]);
        String targetBtnName = tokens[2];

        getPropertyModel().put("v" + index, value);
        children.add(new FileChooserDialogEvent_JFC(model, fileName, fileType, targetBtnName));
        index++;
      }

    }
  }

  @Override
  public String getEventTypeName() {
    return "filechooser event";
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {
    // String isShowing = model.get("showing");
    // if (!"true".equals(isShowing))
    // return false;
    // isShowing = model.get("enabled");
    // if (!"true".equals(isShowing))
    // return false;

    String windowListeners = model.get("windowlisteners");
    if (windowListeners == null || "null".equals(windowListeners))
      return false;

    if (windowListeners.indexOf("javax.swing.JFileChooser") >= 0)
      return true;
    return false;
  }
  @Override
  public int getValueHash() {
     return Hashing.crc32().hashString(fileName, Charsets.UTF_8).asInt();
  }

  @Override
  public boolean stopChainingIfSupported() {
    return true;
  }

  @Override
  public String getActionPropertyString() {
    return "" + fileName + "|" + fileType + "|" + targetBtnName;
  }

  @Override
  public void performImpl(Object... args) {
    if (isContainer)
      return;
    ((Window) getWindowModel().getRef()).requestFocus();

    ComponentModel fileNameCompModel = findFileNameComponent(getComponentModel().getModelNode());
    if (fileNameCompModel == null) {
      TestLogger.error("#A component for the file(folder) name cannot be found!");
      return;
    }
    EditableTextEvent_JFC fileNameEvent = new EditableTextEvent_JFC(fileNameCompModel, fileName);
    fileNameEvent.performImpl();

    if (fileType >= 0) {
      ComponentModel fileTypeCompModel = findFileTypeComponent(getComponentModel().getModelNode());
      if (fileTypeCompModel == null) {
        TestLogger.error("#A component for the file type cannot be found!");
        return;
      }
      SelectionEvent_JFC fileTypeEvent = new SelectionEvent_JFC(fileTypeCompModel, fileType);
      fileTypeEvent.performImpl();
    }

    ComponentModel targetBtnModel = findTargetBtnComponent(getComponentModel().getModelNode());
    if (targetBtnModel == null) {
      TestLogger.error("#A component for the target button cannot be found!");
      return;
    }
    ActionableEvent_JFC targetBtnEvent = new ActionableEvent_JFC(targetBtnModel);
    targetBtnEvent.performImpl();
  }

  private ComponentModel findTargetBtnComponent(GUIModel root) {

    for (Enumeration<GUIModel> enumuration = (Enumeration<GUIModel>) root.depthFirstEnumeration(); enumuration
        .hasMoreElements();) {
      GUIModel node = enumuration.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel model = (ComponentModel) userObj;

        AccessibleAction aAction = JFCUtil.getAccessibleContext(model).getAccessibleAction();
        Accessible accessibleObj = (Accessible) model.getRef();
        if (aAction == null)
          continue;

        if (aAction.getAccessibleActionCount() == 0)
          continue;
        if (accessibleObj instanceof JMenuItem) {
          continue; // should be handled by SelectionMenuEvent
        }
        if (accessibleObj instanceof JTextComponent) {
          continue; // should be handled by EditableTextEvent
        }
        if (accessibleObj instanceof BasicArrowButton) {
          continue; // should be handled by ValueEvnet
        }
        if (accessibleObj instanceof JComboBox) {
          continue; // should be handled by SelectionEvent
        }
        if (accessibleObj instanceof JSpinner) {
          continue; // should be handled by SelectionEvent
        }

        String title = model.get("title");
        if (title.equalsIgnoreCase(targetBtnName)) {
          return model;
        }
      }
    }
    return null;
  }

  private ComponentModel findFileNameComponent(GUIModel root) {
    for (Enumeration<GUIModel> enumuration = (Enumeration<GUIModel>) root.depthFirstEnumeration(); enumuration
        .hasMoreElements();) {
      GUIModel node = enumuration.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel model = (ComponentModel) userObj;

        AccessibleEditableText aValue = JFCUtil.getAccessibleContext(model).getAccessibleEditableText();
        if (aValue == null)
          continue;

        Accessible accessibleObj = (Accessible) model.getRef();
        if (accessibleObj instanceof JTextComponent) {
          if (!((JTextComponent) accessibleObj).isEditable())
            continue;
        }

        String title = model.get("title");
        if (title.equalsIgnoreCase("File Name:")) {
          return model;
        }
        if (title.equalsIgnoreCase("Folder Name:")) {
          return model;
        }
      }
    }

    return null;
  }

  private ComponentModel findFileTypeComponent(GUIModel root) {

    for (Enumeration<GUIModel> enumuration = (Enumeration<GUIModel>) root.depthFirstEnumeration(); enumuration
        .hasMoreElements();) {
      GUIModel node = enumuration.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel model = (ComponentModel) userObj;

        AccessibleSelection aSelection = JFCUtil.getAccessibleContext(model).getAccessibleSelection();
        if (aSelection == null)
          continue;

        if (JFCUtil.getAccessibleContext(model).getAccessibleChildrenCount() <= 0)
          continue;

        Accessible accessibleObj = (Accessible) model.getRef();
        if (accessibleObj instanceof JMenuItem) // should be handled by SelectionMenuEvent
          continue;
        if (accessibleObj instanceof JTree) // this event may handle, but should be handled by ActionableEvent
          continue;
        if (accessibleObj instanceof JTable) // this event may handle, but should be handled by ActionableEvent
          continue;
        if (accessibleObj instanceof JMenuBar) // should be handled by SelectionMenuEvent
          continue;

        String title = model.get("title");
        String className = model.get("class");
        if (title.equalsIgnoreCase("Files of Type:") && className.equalsIgnoreCase("javax.swing.JComboBox")) {
          return model;
        }
      }
    }
    return null;
  }

  // --- implement Converter interface ---
  @Override
  public boolean canConvert(Class clazz) {
    return FileChooserDialogEvent_JFC.class.equals(clazz);
  }

  @Override
  protected void marshalAttributes(EventModel modelEvent, HierarchicalStreamWriter writer) {
    super.marshalAttributes(modelEvent, writer);

    writer.startNode("fileName");
    writer.setValue("" + ((FileChooserDialogEvent_JFC) modelEvent).fileName);
    writer.endNode();

    writer.startNode("fileType");
    writer.setValue("" + ((FileChooserDialogEvent_JFC) modelEvent).fileType);
    writer.endNode();

    writer.startNode("targetBtnName");
    writer.setValue("" + ((FileChooserDialogEvent_JFC) modelEvent).targetBtnName);
    writer.endNode();

  }

  @Override
  protected void unmarshalAttributes(EventModel event, HierarchicalStreamReader reader) {
    super.unmarshalAttributes(event, reader);
    if (reader.hasMoreChildren()) {
      reader.moveDown();
      ((FileChooserDialogEvent_JFC) event).fileName = reader.getValue();
      reader.moveUp();
    }
    if (reader.hasMoreChildren()) {
      reader.moveDown();
      ((FileChooserDialogEvent_JFC) event).fileType = Integer.valueOf(reader.getValue());
      reader.moveUp();
    }
    if (reader.hasMoreChildren()) {
      reader.moveDown();
      ((FileChooserDialogEvent_JFC) event).targetBtnName = reader.getValue();
      reader.moveUp();
    }
  }

}
