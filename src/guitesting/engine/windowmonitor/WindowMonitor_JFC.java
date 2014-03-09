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
package guitesting.engine.windowmonitor;

import guitesting.model.WindowModel;
import guitesting.ui.GUITester;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class WindowMonitor_JFC extends WindowMonitor {

  boolean isStarted = false;
  WindowListener listener = null;
  LinkedList<WindowModel> openedWindows = null;
  LinkedList<WindowModel> closedWindows = null;
  Toolkit toolkit = null;

  public WindowMonitor_JFC() {
    init();
  }

  public class WindowListener implements AWTEventListener {

    @Override
    public void eventDispatched(AWTEvent event) {
      int id = event.getID();
      Window window = ((WindowEvent) event).getWindow();
      WindowModel winModel = null;

      switch (id) {
      case WindowEvent.WINDOW_OPENED:
        winModel = GUITester.getInstance().getGuiModelExtractor().extractWindowInfo(window);
        openedWindows.add(winModel);
        break;
      case WindowEvent.WINDOW_CLOSED:
        winModel = GUITester.getInstance().getGuiModelExtractor().extractWindowInfo(window);
        closedWindows.add(winModel);
      }
    }
  }

  @Override
  public void init() {
    if (isStarted) {
      stop();
      isStarted = false;
    }
    toolkit = java.awt.Toolkit.getDefaultToolkit();
    listener = new WindowListener();
    openedWindows = new LinkedList<WindowModel>();
    closedWindows = new LinkedList<WindowModel>();

  }

  @Override
  public void reset() {
    if (isStarted) {
      stop();
      isStarted = false;
    }
    openedWindows.clear();
    closedWindows.clear();
  }

  @Override
  public void start() {
    if (!isStarted) {
      toolkit.addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);
    }
    isStarted = true;
  }

  @Override
  public void stop() {

    if (isStarted) {
      toolkit.removeAWTEventListener(listener);
    }
    isStarted = false;

  }

  @Override
  public LinkedList<WindowModel> getOpenedWindows() {
    return openedWindows;
  }

  @Override
  public LinkedList<WindowModel> getClosedWindows() {
    return closedWindows;
  }

  @Override
  public void closeWindow(WindowModel openedWindow) {
    Window window = (Window) openedWindow.getRef();

    if (window != null && window.isDisplayable() && window.isShowing()) {

      window.dispose();
    }
  }

}
