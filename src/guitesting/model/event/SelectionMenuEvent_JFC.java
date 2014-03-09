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
import guitesting.ui.GUITester;
import guitesting.util.JFCUtil;

import java.awt.Window;
import java.util.ArrayList;

import javax.accessibility.Accessible;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
public class SelectionMenuEvent_JFC extends EventModel {
  private static final long serialVersionUID = 1L;

  public SelectionMenuEvent_JFC(ComponentModel model) {
    super(model);
  }

  @Override
  public void performImpl(Object... args) {
    ((Window) getWindowModel().getRef()).requestFocus();
    ArrayList<AbstractButton> eventSequence = new ArrayList<AbstractButton>();
    GUIModel currentNode = (GUIModel) componentModel.getModelNode();
    while (currentNode != null) {
      ComponentModel currentModel = (ComponentModel) currentNode.getUserObject();
      if (!(currentModel.getRef() instanceof JMenuItem))
        break;
      eventSequence.add((AbstractButton) currentModel.getRef());
      currentNode = (GUIModel) currentNode.getParent();
    }

    int len = eventSequence.size();
    for (int i = len - 1; i >= 0; i--) {
      if (eventSequence.get(i) instanceof JMenu) {
        JMenu menu = ((JMenu) eventSequence.get(i));
        // new JMenuOperator(menu).clickMouse();
        if (menu.isPopupMenuVisible())
          menu.setPopupMenuVisible(false);
        menu.setPopupMenuVisible(true);
        JFCUtil.waitForEventIdle();        
      }
    }

    // new JMenuItemOperator((JMenuItem) eventSequence.get(0)).clickMouse();

    eventSequence.get(0).doClick();
    GUITester.getInstance().getDelayManager().delayEventIntervalTime(10);

    // remove pop-up menu since pop-up menu is shown accidentally after programmatically executing a "click"
    for (int i = 0; i < len; i++) {
      if (eventSequence.get(i) instanceof JMenu) {
        // new JMenuOperator(((JMenu) eventSequence.get(i))).setPopupMenuVisible(false);
        ((JMenu) eventSequence.get(i)).setPopupMenuVisible(false);
        JFCUtil.waitForEventIdle();
      }
    }
  }

  @Override
  public String getEventTypeName() {
    return "menu selection event";
  }
  
  @Override
  public int getValueHash() {
    return 0;
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {

    Accessible accessible = (Accessible) model.getRef();
    if (!(accessible instanceof JMenuItem) || accessible instanceof JMenu)
      return false;

    GUIModel currentNode = (GUIModel) model.getModelNode();
    if (currentNode == null)
      return false;
    while (currentNode != null) {
      ComponentModel currentModel = (ComponentModel) currentNode.getUserObject();
      if (currentModel == null)
        return false;
      // check if the menu is reachable
      if (!(currentModel.getRef() instanceof JMenuItem)) {
        return true;
      } else {
      }
      currentNode = (GUIModel) currentNode.getParent();
    }
    return false;
  }

  @Override
  public boolean canConvert(Class clazz) {
    return SelectionMenuEvent_JFC.class.equals(clazz);
  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

}
