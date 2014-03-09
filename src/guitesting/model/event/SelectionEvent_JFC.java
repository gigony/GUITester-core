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

import java.awt.Window;
import java.util.ArrayList;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleSelection;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("event")
public class SelectionEvent_JFC extends EventModel {
  protected int chooseIndex = 0;

  public SelectionEvent_JFC(ComponentModel model, int index) {
    super(model);
    setChooseIndex(index);
  }

  public SelectionEvent_JFC(ComponentModel model) {
    super(model);

    if (model != null) {
      Map<String, String> customValues = TestProperty.eventValueManager.getMatchedCustomValues(this);
      ArrayList<String> targetValues = new ArrayList<String>();

      Accessible accessibleObj = (Accessible) model.getRef();
      isContainer = true;

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
        int itemCount = -1;
        if (accessibleObj instanceof JComboBox) {
          // default values
          JComboBox comboBox = (JComboBox) accessibleObj;
          itemCount = comboBox.getItemCount();

        } else {// JTabbedPane, etc.
          // default values
          AccessibleContext aContext = JFCUtil.getAccessibleContext(componentModel);
          itemCount = aContext.getAccessibleChildrenCount();
        }
        children = new ArrayList<EventModel>(itemCount);
        for (int i = 0; i < itemCount; i++) {
          targetValues.add("" + i);
        }
      }

      children = new ArrayList<EventModel>(targetValues.size());
      int index = 1;
      for (String value : targetValues) {
        getPropertyModel().put("v" + index, value);
        children.add(new SelectionEvent_JFC(model, Integer.valueOf(value)));
        index++;
      }

    }
  }

  @Override
  public void performImpl(Object... args) {

    if (isContainer)
      return;
    ((Window) getWindowModel().getRef()).requestFocus();

    AccessibleSelection aSelection = JFCUtil.getAccessibleContext(componentModel).getAccessibleSelection();
    Accessible accessibleObj = (Accessible) componentModel.getRef();

    if (accessibleObj instanceof JTabbedPane)// AccessibleRole.PAGE_TAB_LIST
    {
      ((JTabbedPane) accessibleObj).setSelectedIndex(getChooseIndex());
      // TestLogger.info("\t@Do Tab selection:" + getChooseIndex());

    } else if (accessibleObj instanceof JComboBox) {
      JComboBox comboBox = (JComboBox) accessibleObj;
      comboBox.setSelectedIndex(getChooseIndex());
      // TestLogger.info("\t@Do ComboBox item selection:" + getChooseIndex());
    } else {
      if (aSelection.isAccessibleChildSelected(getChooseIndex()))
        aSelection.removeAccessibleSelection(getChooseIndex());
      else
        aSelection.addAccessibleSelection(getChooseIndex());
      // TestLogger.info("\t@Do selection:" + getChooseIndex());

    }
  }

  @Override
  public String getEventTypeName() {
    return "selection event";
  }

  @Override
  public String getActionPropertyString() {
    return "" + chooseIndex;
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {
    // String isShowing = model.get("showing");
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

    AccessibleSelection aSelection = JFCUtil.getAccessibleContext(model).getAccessibleSelection();
    if (aSelection == null)
      return false;

    if (JFCUtil.getAccessibleContext(model).getAccessibleChildrenCount() <= 0)
      return false;

    Accessible accessibleObj = (Accessible) model.getRef();
    if (accessibleObj instanceof JMenuItem) // should be handled by SelectionMenuEvent
      return false;
    if (accessibleObj instanceof JTree) // this event may handle, but should be handled by ActionableEvent
      return false;
    if (accessibleObj instanceof JTable) // this event may handle, but should be handled by ActionableEvent
      return false;
    if (accessibleObj instanceof JMenuBar) // should be handled by SelectionMenuEvent
      return false;
    return true;
  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

  public void setChooseIndex(int chooseIndex) {
    this.chooseIndex = chooseIndex;
  }

  public int getChooseIndex() {
    return chooseIndex;
  }

  @Override
  public int getValueHash() {
    return chooseIndex * 13;
  }

  @Override
  public boolean canConvert(Class clazz) {
    return SelectionEvent_JFC.class.equals(clazz);
  }
  
  

  @Override
  protected void marshalAttributes(EventModel modelEvent, HierarchicalStreamWriter writer) {
    super.marshalAttributes(modelEvent, writer);
    writer.startNode("chooseIndex");
    writer.setValue("" + ((SelectionEvent_JFC) modelEvent).chooseIndex);
    writer.endNode();

  }

  @Override
  protected void unmarshalAttributes(EventModel event, HierarchicalStreamReader reader) {
    super.unmarshalAttributes(event, reader);
    reader.moveDown();
    ((SelectionEvent_JFC) event).chooseIndex = Integer.valueOf(reader.getValue());
    reader.moveUp();
  }

}
