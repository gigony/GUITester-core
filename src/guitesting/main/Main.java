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
package guitesting.main;

import static org.kohsuke.args4j.ExampleMode.ALL;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import guitesting.engine.StrategyManager;
import guitesting.engine.strategy.AbstractStrategy;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
  public static void main(String args[]) {    
    // set default properties before parsing arguments
    TestProperty.setDefaultLog4jProperties();

    parseArguments(args);

    // initialize eventHandlers,converters, etc
    TestProperty.initialize();

    Thread.currentThread().setContextClassLoader(TestProperty.pluginClassLoader);
    AbstractStrategy strategy = StrategyManager.getInstance().getStrategy(
        TestProperty.propertyStore.get("guitester.strategy"));
    TestLogger.debug("Strategy: %s",strategy.getClass().getName());
    TestLogger.debug("main args: %s",TestProperty.propertyStore.get("guitester.main_args", ""));
    TestLogger.debug("strategy args: %s",TestProperty.propertyStore.get("guitester.strategy_args", ""));
    
    String[] mainArgs = TestProperty.propertyStore.get("guitester.main_args", "").split(" ");

    strategy.run(mainArgs);
  }

  private static void parseArguments(String[] args) {
    TestLogger.debug_("Command line arguments: ");
    for (String arg : args)
      TestLogger.debug_(arg + " ");
    TestLogger.debug("");

    // parse arguments
    CmdLineParser parser = new CmdLineParser(new TestProperty());
    parser.setUsageWidth(80);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      System.err.println("java GUITester [options...] arguments...");
      // print the list of available options
      parser.printUsage(System.err);
      System.err.println();
      // print option sample. This is useful some time
      System.err.println("  Example: GUITester SampleMain" + parser.printExample(ALL));
      return;
    }

  }
}
