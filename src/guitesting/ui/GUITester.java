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
package guitesting.ui;

import guitesting.engine.appmanager.ApplicationManager;
import guitesting.engine.componentnotifier.ComponentNotifier;
import guitesting.engine.delaymanager.DelayManager;
import guitesting.engine.idgenerator.IDGenerator;
import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.engine.testcaseexecutor.TestCaseExecutor;
import guitesting.engine.windowmonitor.WindowMonitor;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

public class GUITester {

  private volatile static GUITester instance;

  private GUITesterFrame ui = null;

  private ApplicationManager applicationManager = null;
  private DelayManager delayManager = null;
  private WindowMonitor windowMonitor = null;
  private GUIModelExtractor guiModelExtractor = null;
  private IDGenerator idGenerator = null;
  private TestCaseExecutor testCaseExecutor = null;
  private ComponentNotifier componentNotifier = null;

  private UIAction uiAction = null;

  private GUITester() {
  }

  public static GUITester getInstance() {
    if (instance == null) {
      synchronized (GUITester.class) {
        if (instance == null) {
          instance = new GUITester();
        }
      }
    }
    return instance;
  }

  public void setApplicationManager(ApplicationManager manager) {
    this.applicationManager = manager;
    TestLogger.debug("\tApplication manager plugin: %s is set.", manager.getClass().getName());

  }

  public ApplicationManager getApplicationManager() {
    return applicationManager;
  }

  public void setDelayManager(DelayManager manager) {
    this.delayManager = manager;
    TestLogger.debug("\tDelay manager plugin: %s is set.", manager.getClass().getName());
  }

  public DelayManager getDelayManager() {
    return delayManager;
  }

  public void setWindowMonitor(WindowMonitor monitor) {
    this.windowMonitor = monitor;
    TestLogger.debug("\tWindow monitor plugin: %s is set.", monitor.getClass().getName());
  }

  public WindowMonitor getWindowMonitor() {
    return windowMonitor;
  }

  public void setGuiModelExtractor(GUIModelExtractor modelExtractor) {
    this.guiModelExtractor = modelExtractor;
    TestLogger.debug("\tGUI model extractor plugin: %s is set.", modelExtractor.getClass().getName());
  }

  public GUIModelExtractor getGuiModelExtractor() {
    return guiModelExtractor;
  }

  public void setIdGenerator(IDGenerator idGenerator) {
    this.idGenerator = idGenerator;
    TestLogger.debug("\tIDGenerator plugin: %s is set.", idGenerator.getClass().getName());
  }

  public IDGenerator getIdGenerator() {
    return idGenerator;
  }

  public void setTestCaseExecutor(TestCaseExecutor testCaseExecutor) {
    this.testCaseExecutor = testCaseExecutor;
    TestLogger.debug("\tTest case executor plugin: %s is set.", testCaseExecutor.getClass().getName());
  }

  public TestCaseExecutor getTestCaseExecutor() {
    return testCaseExecutor;
  }

  public void setComponentNotifier(ComponentNotifier componentNotifier) {
    this.componentNotifier = componentNotifier;
    TestLogger.debug("\tComponent notifier plugin: %s is set.", componentNotifier.getClass().getName());
  }

  public ComponentNotifier getComponentNotifier() {
    return componentNotifier;
  }

  public void setUIAction(UIAction uiAction) {
    this.uiAction = uiAction;
  }

  public UIAction getUIAction() {
    return uiAction;
  }

  public void launchGUITesterUI() {
    launchGUITesterUI(null);
  }

  public void launchGUITesterUI(UIAction uiAction) {
    setUIAction(uiAction);
    // if we use GUI interface

    if (TestProperty.propertyStore.getBoolean("guitester.gui", false)) {
      if (this.ui != null) {
        this.ui.dispose();
        this.ui = null;
      }
      GUITesterFrame ui = new GUITesterFrame(this, "GUITester");
      this.ui = ui;
      // set to ignore GUITester window
      GUITester.getInstance().getGuiModelExtractor().setTesterFrame(ui);

      // set to highlight a pointing component in the list.
      ui.setComponentNotifier(GUITester.getInstance().getComponentNotifier());
    }

  }

}
