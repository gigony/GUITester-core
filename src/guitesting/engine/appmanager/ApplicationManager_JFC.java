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
package guitesting.engine.appmanager;

import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.lang.reflect.Method;

import com.google.common.hash.HashCode;

public class ApplicationManager_JFC extends ApplicationManager {

  @Override
  public boolean runApp() {
    String className = TestProperty.propertyStore.get("guitester.main", "");
    String[] mainArgs = TestProperty.propertyStore.get("guitester.main_args", "").split(" ");
    Method main;

    // launch application
    try {

      Class<?> c = Class.forName(className, true, TestProperty.appClassLoader);
      Class<?>[] argTypes = new Class[] { String[].class };
      main = c.getDeclaredMethod("main", argTypes);

      main.invoke(null, (Object) mainArgs);

    } catch (ClassNotFoundException e) {
      TestLogger.error("class " + className + " cannot be found!");
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;

  }

}
