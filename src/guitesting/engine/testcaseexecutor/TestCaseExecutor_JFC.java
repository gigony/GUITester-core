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

import guitesting.util.MySecurityManager;

import java.lang.Thread.UncaughtExceptionHandler;

public class TestCaseExecutor_JFC extends TestCaseExecutor {

  final MySecurityManager securityManager = new MySecurityManager();
  UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

  public void beginProcess() {
    securityManager.disableExitMethod(true);

    // set DefaultUncaughtExceptionHandler
    final Runnable handler = errorHandler;
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        securityManager.disableExitMethod(false);
        if (handler != null)
          handler.run();
        System.exit(1);
      }
    });
  }

  public void caughtException() {
    securityManager.disableExitMethod(false);
  }

  public void endProcess() {
    securityManager.disableExitMethod(false);

    // restore DefaultUncaughtExceptionHandler
    Thread.setDefaultUncaughtExceptionHandler(oldHandler);
  }

}
