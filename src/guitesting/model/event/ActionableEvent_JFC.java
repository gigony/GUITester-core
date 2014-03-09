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

import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.model.ComponentModel;
import guitesting.model.WindowModel;
import guitesting.util.JFCUtil;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.JTextComponent;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
public class ActionableEvent_JFC extends EventModel {

  public ActionableEvent_JFC(ComponentModel model) {
    super(model);
  }

  @Override
  public void performImpl(Object... args) {
    ((Window) getWindowModel().getRef()).requestFocus();

    final AccessibleContext aContext = JFCUtil.getAccessibleContext(componentModel);
    AccessibleAction aAction = aContext.getAccessibleAction();
    Accessible accessibleobj = (Accessible) getAccessibleComponent();

    if (accessibleobj instanceof Component) {
      Component comp = (Component) accessibleobj;

      int w = comp.getWidth();
      int h = comp.getHeight();
      int x = w / 2;
      int y = h / 2;
      MouseEvent mouseMoveEvent = new MouseEvent(comp, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 16, x, y, 1,
          false, MouseEvent.BUTTON1);
      comp.dispatchEvent(mouseMoveEvent);
      MouseEvent mousePressEvent = new MouseEvent(comp, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 16, x, y,
          1, false, MouseEvent.BUTTON1);
      comp.dispatchEvent(mousePressEvent);
      MouseEvent mouseReleaseEvent = new MouseEvent(comp, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 16, x,
          y, 1, false, MouseEvent.BUTTON1);
      comp.dispatchEvent(mouseReleaseEvent);
      MouseEvent mouseClickEvent = new MouseEvent(comp, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 16, x, y,
          1, false, MouseEvent.BUTTON1);
      comp.dispatchEvent(mouseClickEvent);

    } else {
      aAction.doAccessibleAction(0); // only first event will be executed.
    }

  }

  @Override
  public int getValueHash() {
    return 0;
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {

    // skip fileChooser dialog.
    WindowModel winModel = GUIModelExtractor.getWindowModel(model);
    if (winModel != null && winModel.get("windowlisteners") != null
        && winModel.get("windowlisteners").indexOf("javax.swing.JFileChooser") >= 0) {
      return false;
    }

    AccessibleAction aAction = JFCUtil.getAccessibleContext(model).getAccessibleAction();
    Accessible accessibleObj = (Accessible) model.getRef();
    if (aAction == null)
      return false;

    if (aAction.getAccessibleActionCount() == 0)
      return false;
    if (accessibleObj instanceof JMenuItem) {
      return false; // should be handled by SelectionMenuEvent
    }
    if (accessibleObj instanceof JTextComponent) {
      return false; // should be handled by EditableTextEvent
    }
    if (accessibleObj instanceof BasicArrowButton) {
      return false; // should be handled by ValueEvnet
    }
    if (accessibleObj instanceof JComboBox) {
      return false; // should be handled by SelectionEvent
    }
    if (accessibleObj instanceof JSpinner) {
      return false; // should be handled by SelectionEvent
    }

    return true;

  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

  @Override
  public String getEventTypeName() {
    return "action event";
  }

  @Override
  public boolean canConvert(Class clazz) {
    return ActionableEvent_JFC.class.equals(clazz);
  }
}
