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

import guitesting.engine.strategy.AbstractStrategy;
import guitesting.engine.strategy.DefaultStrategy;
import guitesting.util.TestLogger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class StrategyManager {
  private static StrategyManager instance;

  public static StrategyManager getInstance() {
    if (instance == null) {
      synchronized (StrategyManager.class) {
        if (instance == null) {
          instance = new StrategyManager();
        }
      }
    }
    return instance;
  }

  private StrategyManager() {
  }

  Map<String, Class<? extends AbstractStrategy>> strategyClassMap = new HashMap<String, Class<? extends AbstractStrategy>>();

  public void init() {
    strategyClassMap.clear();
  }

  public void registerStrategyClass(String handlerName, Class<? extends AbstractStrategy> cl) {
    TestLogger.debug("\tStrategy plugin... %s", handlerName);
    strategyClassMap.put(handlerName, cl);
  }

  public AbstractStrategy getStrategy(String name) {
    // default strategy is DefaultStrategy class.
    if (name == null || "".equals(name)) {
      return new DefaultStrategy();

    }

    Class<? extends AbstractStrategy> strategyClass = strategyClassMap.get(name);
    // if the class cannot be found in the plugin pool, try to get the class from built-in classes.
    if (strategyClass == null) {
      try {
        strategyClass = (Class<? extends AbstractStrategy>) Class.forName("guitesting.engine.strategy." + name);
      } catch (ClassNotFoundException e) {
        try {
          strategyClass = (Class<? extends AbstractStrategy>) Class.forName(name);
        } catch (ClassNotFoundException e1) {
          TestLogger.error("%s", e1.getMessage());
          e.printStackTrace();
        }
      }

      if (strategyClass == null)
        return null;
    }

    // get instance
    try {
      Constructor<? extends AbstractStrategy> constructor = strategyClass.getConstructor(new Class<?>[] {});
      AbstractStrategy strategy = (AbstractStrategy) constructor.newInstance();
      return strategy;
    } catch (Throwable t) {
      TestLogger.error("%s", t.getMessage());
      t.printStackTrace();
    }
    return null;

  }
}
