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
package guitesting.engine.testcaseexecutor;

import guitesting.engine.delaymanager.DelayManager;
import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.engine.monitors.IExecutionMonitor;
import guitesting.model.GUIModel;
import guitesting.model.ITestCaseElement;
import guitesting.model.event.EventModel;
import guitesting.ui.GUITester;
import guitesting.util.MySecurityManager.ExitCaughtedException;
import guitesting.util.TestLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class TestCaseExecutor {
  List<IExecutionMonitor> monitors = new ArrayList<IExecutionMonitor>();

  protected Runnable errorHandler = null;

  public boolean execute(Object testcaseModel) {
    if (!(testcaseModel instanceof List<?>))
      TestLogger.error("test case is not an instance of 'List' class!");

    GUITester tester = GUITester.getInstance();
    DelayManager delayManager = tester.getDelayManager();
    GUIModelExtractor extractor = tester.getGuiModelExtractor();

    try {
      beginProcess();

      delayManager.delayInitialTime();

      GUIModel guiModel = extractor.getGUIModel();

      beforeExecution(testcaseModel, guiModel);

      List<ITestCaseElement> testCase = (List<ITestCaseElement>) testcaseModel;
      int count = testCase.size();
      boolean isExited = false;
      EventModel nextEvent = null;
      for (int stepIndex = 0; stepIndex < count; stepIndex++) {

        TestLogger.info(String.format("Executing..(event index:%d)", stepIndex));

        if (stepIndex != 0) {
          guiModel = extractor.getGUIModel();
        }
        List<EventModel> availableEvents = guiModel.getEvents();
        ITestCaseElement nextEventToExecute = testCase.get(stepIndex);

        // find matching event
        for (EventModel availableEvent : availableEvents) {

          if (nextEventToExecute.isMatched(availableEvent)) {
            nextEvent = availableEvent;
            break;
          }
        }

        // if event is not existed
        if (nextEvent == null) {
          TestLogger.info("\tEvent index %d : %s(%s) doesn't exist!", stepIndex, nextEventToExecute.getEventName(),
              nextEventToExecute.getHashCode());
          break;
        } else {
          TestLogger.info("\tEvent index %d : %s(%s) is found...", stepIndex, nextEventToExecute.getEventName(),
              nextEventToExecute.getHashCode());
        }

        beforeStep(testcaseModel, stepIndex, nextEvent, guiModel);
        try {
          // execute event
          nextEvent.perform();
        } catch (ExitCaughtedException e) {
          isExited = true;
          TestLogger.info("\tSystem.exit() is caught..skip");
        }

        delayManager.delayEventIntervalTime();

        guiModel = extractor.getGUIModel();

        // do this even though program was exited because of post-processing
        afterStep(testcaseModel, stepIndex, nextEvent, guiModel, isExited);

        TestLogger.info(String.format("\tSucceed!(event index:%d)- %s", stepIndex,
            nextEvent.getComponentModel().get("title")));

        if (isExited)
          break;
      }
      afterExecution(testcaseModel, nextEvent, guiModel, isExited);

    } catch (Throwable t) {
      caughtException();

      return false;
    }
    endProcess();

    return true;
  }

  public abstract void caughtException();

  public abstract void beginProcess();

  public abstract void endProcess();

  private void beforeExecution(Object testcase, GUIModel guiModel) {
    for (IExecutionMonitor monitor : monitors)
      monitor.beforeExecution(testcase, guiModel);
  }

  private void beforeStep(Object testcase, int stepIndex, EventModel event, GUIModel guiModel) {
    for (IExecutionMonitor monitor : monitors)
      monitor.beforeStep(testcase, stepIndex, event, guiModel);
  }

  private void afterStep(Object testcase, int stepIndex, EventModel event, GUIModel guiModel, boolean isExited) {
    for (IExecutionMonitor monitor : monitors)
      monitor.afterStep(testcase, stepIndex, event, guiModel, isExited);
  }

  private void afterExecution(Object testcase, EventModel event, GUIModel guiModel, boolean isExited) {
    for (IExecutionMonitor monitor : monitors)
      monitor.afterExecution(testcase, event, guiModel, isExited);
  }

  public void setMonitors(List<IExecutionMonitor> monitors) {
    if (monitors == null)
      monitors = new ArrayList<IExecutionMonitor>();

    this.monitors = monitors;
  }

  public boolean addMonitor(IExecutionMonitor monitor) {
    return monitors.add(monitor);
  }

  public IExecutionMonitor getMonitor(int index) {
    return monitors.get(index);
  }

  public boolean removeMonitor(IExecutionMonitor monitor) {
    return monitors.remove(monitor);
  }

  public void removeMonitor(int index) {
    monitors.remove(index);
  }

  public int monitorCount() {
    return monitors.size();
  }

  public Runnable getErrorHandler() {
    return errorHandler;
  }

  public void setErrorHandler(Runnable errorHandler) {
    this.errorHandler = errorHandler;
  }
}
