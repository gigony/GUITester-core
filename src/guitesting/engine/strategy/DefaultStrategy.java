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
package guitesting.engine.strategy;

import guitesting.engine.monitors.IExecutionMonitor;
import guitesting.engine.testcaseexecutor.TestCaseExecutor;
import guitesting.ui.GUITester;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultStrategy extends AbstractStrategy {

  @Override
  public void run(String[] mainArgs) {

    // launch GUITester
    GUITester tester = GUITester.getInstance();
    TestCaseExecutor testCaseExecutor = tester.getTestCaseExecutor();
    tester.launchGUITesterUI();

    TestLogger.info("Run Application:" + TestProperty.propertyStore.get("guitester.main", ""));

    // launch application
    tester.getApplicationManager().runApp();

    // if execution mode
    final String exePath = TestProperty.propertyStore.get("execute");
    if (!exePath.equals("")) {
      File testCaseFile = new File(exePath);
      if (!testCaseFile.exists()) {
        TestLogger.error("The testcase file " + exePath + " does not exist!");
        return;
      }

      try {
        FileInputStream testCaseStream = new FileInputStream(testCaseFile);

        final Object testCase = TestProperty.xStream.fromXML(testCaseStream);
        final TestCaseExecutor executor = testCaseExecutor;

        executor.setMonitors(getMonitorObjects());
        executor.setErrorHandler(getErrorHandlerObject());

        // execute
        Thread thread = new Thread(new Runnable() {
          @Override
          public void run() {
            executor.execute(testCase);
          }
        });
        thread.start();
        thread.join();

      } catch (Exception e) {
        e.printStackTrace();
        TestLogger.log.error(e);
      } finally {
        System.exit(0);
      }
    } else // run mode
    {
      tester.setUIAction(null);
    }
  }

  private List<IExecutionMonitor> getMonitorObjects() {
    List<IExecutionMonitor> result = new ArrayList<IExecutionMonitor>();
    String[] handlerNameList = TestProperty.propertyStore.get("monitors").split("[\\:\\;]");
    if (handlerNameList.length == 0) {
      TestLogger.error("  There is no monitor list!");
      return new ArrayList<IExecutionMonitor>();
    }

    for (String monitortHandlerName : handlerNameList) {
      try {
        Class<?> handlerClass = null;

        if (TestProperty.pluginClassLoader.containsClass(monitortHandlerName)) {
          handlerClass = (Class<?>) TestProperty.pluginClassLoader.loadClass(monitortHandlerName);
        } else {
          handlerClass = (Class<?>) Class.forName("guitesting.engine.monitors." + monitortHandlerName);
        }
        if (IExecutionMonitor.class.isAssignableFrom(handlerClass)) {
          result.add((IExecutionMonitor) handlerClass.newInstance());
        }

      } catch (Throwable t) {
        t.printStackTrace();
        TestLogger.warn(String.format("\tevent %s ... Fail(%s)", monitortHandlerName, t));
      }
    }
    return result;
  }

  private Runnable getErrorHandlerObject() {
    String handlerName = TestProperty.propertyStore.get("error_handler");

    if (handlerName == null)
      return null;

    try {
      Class<?> handlerClass = null;

      if (TestProperty.pluginClassLoader.containsClass(handlerName)) {
        handlerClass = (Class<?>) TestProperty.pluginClassLoader.loadClass(handlerName);
      }

      if (Runnable.class.isAssignableFrom(handlerClass))
        return (Runnable) handlerClass.newInstance();

    } catch (Throwable t) {
      t.printStackTrace();
      TestLogger.warn(String.format("\tFailed loading an error handler(%s)", handlerName, t.getMessage()));
    }

    return null;
  }
}
