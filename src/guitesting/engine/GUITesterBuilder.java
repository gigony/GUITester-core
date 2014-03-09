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
package guitesting.engine;

import guitesting.engine.appmanager.ApplicationManager;
import guitesting.engine.componentnotifier.ComponentNotifier;
import guitesting.engine.delaymanager.DelayManager;
import guitesting.engine.idgenerator.IDGenerator;
import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.engine.testcaseexecutor.TestCaseExecutor;
import guitesting.engine.windowmonitor.WindowMonitor;
import guitesting.ui.GUITester;
import guitesting.util.TestLogger;

public class GUITesterBuilder {
  String platform = null;

  public GUITesterBuilder(String platform) {
    this.platform = platform;
  }

  public ApplicationManager makeApplicationManager() {
    return (ApplicationManager) getClassInstance("guitesting.engine.appmanager.ApplicationManager_" + platform);
  }

  public DelayManager makeDelayManager() {
    return (DelayManager) getClassInstance("guitesting.engine.delaymanager.DelayManager_" + platform);
  }

  public WindowMonitor makeWindowMonitor() {
    return (WindowMonitor) getClassInstance("guitesting.engine.windowmonitor.WindowMonitor_" + platform);
  }

  public GUIModelExtractor makeGUIModelExtractor() {
    return (GUIModelExtractor) getClassInstance("guitesting.engine.modelextractor.GUIModelExtractor_" + platform);
  }

  public IDGenerator makeIDGenerator() {
    return (IDGenerator) getClassInstance("guitesting.engine.idgenerator.IDGenerator_" + platform);
  }

  public TestCaseExecutor makeTestCaseExecutor() {
    return (TestCaseExecutor) getClassInstance("guitesting.engine.testcaseexecutor.TestCaseExecutor_" + platform);
  }

  public ComponentNotifier makeComponentNotifier() {
    return (ComponentNotifier) getClassInstance("guitesting.engine.componentnotifier.ComponentNotifier_" + platform);
  }

  public void construct() {
    GUITester guiTester = GUITester.getInstance();
    TestLogger.debug("Initialize built-in platform plugins:");
    guiTester.setApplicationManager(makeApplicationManager());
    guiTester.setDelayManager(makeDelayManager());
    guiTester.setWindowMonitor(makeWindowMonitor());
    guiTester.setGuiModelExtractor(makeGUIModelExtractor());
    guiTester.setIdGenerator(makeIDGenerator());
    guiTester.setTestCaseExecutor(makeTestCaseExecutor());
    guiTester.setComponentNotifier(makeComponentNotifier());
  }

  private Object getClassInstance(String className) {
    try {
      Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(className);
      return cl.newInstance();
    } catch (Exception e) {
      TestLogger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
}
