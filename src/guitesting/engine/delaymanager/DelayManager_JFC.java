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
package guitesting.engine.delaymanager;

import guitesting.util.ObjRef;
import guitesting.util.RunnableMethod;
import guitesting.util.TestLogger;

import java.awt.EventQueue;

public class DelayManager_JFC extends DelayManager {

  @Override
  public boolean delayInitialTime(long initialDelay) {
    return delayTime(initialDelay);
  }

  @Override
  public boolean delayEventIntervalTime(long executionDelay) {
    return delayTime(executionDelay);
  }

  @Override
  public boolean delayTime(long time) {
    waitForEventIdle();
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    waitForEventIdle();
    return true;
  }

  public static void waitForEventIdle() {
    // the idea is from google testing blog.

    if (EventQueue.isDispatchThread())
      return;

    ObjRef probe = new ObjRef(false);

    // insert probe
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // insert prove again
    probe.setRef(false);
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
