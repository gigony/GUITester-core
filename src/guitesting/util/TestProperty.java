/*******************************************************************************
 * Copyright (c) 2010-2011, Gigon Bae
 * Copyright (c) 2010-2011, Gigon Bae
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  
 *     1. Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *     
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *     3. Neither the name of this project nor the names of its contributors may be
 *        used to endorse or promote products derived from this software without
 *        specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package guitesting.util;

import guitesting.engine.EventFilter;
import guitesting.engine.EventManager;
import guitesting.engine.EventValueManager;
import guitesting.engine.GUITesterBuilder;
import guitesting.engine.StrategyManager;
import guitesting.engine.appmanager.ApplicationManager;
import guitesting.engine.componentnotifier.ComponentNotifier;
import guitesting.engine.delaymanager.DelayManager;
import guitesting.engine.idgenerator.IDGenerator;
import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.engine.strategy.AbstractStrategy;
import guitesting.engine.windowmonitor.WindowMonitor;
import guitesting.main.Main;
import guitesting.model.ComponentModel;
import guitesting.model.EventListModel;
import guitesting.model.GUIModel;
import guitesting.model.event.EventModel;
import guitesting.ui.GUITester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.common.base.Joiner;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

public class TestProperty {
  public static final String FolderName_Binary = "bin";
  public static final String FolderName_Source = "src";
  public static final String FolderName_Configuration = "config";
  public static final String FolderName_Instrumented = "instrumented";
  public static final String FolderName_Libraries = "lib";
  public static final String FolderName_AUTExtLibraries = "aut_ext";
  public static final String FolderName_PlugIns = "plugins";
  public static final String FolderName_TestSuite = "testsuite";

  public static final String FolderName_Workspace = "workspace";
  public static final String FolderName_InputFile = "input_files";
  public static final String FolderName_OutputFile = "output_files";
  public static final String FolderName_Result = "result";
  public static final String FolderName_Error = "errors";
  public static final String FolderName_Coverage = "coverages";
  public static final String FolderName_MetaData = "metadata";
  public static final String FolderName_Trace = "traces";
  public static final String FolderName_WindowImage = "windowImages";
  public static final String FolderName_ComponentImage = "componentImages";
  public static final String FolderName_Report = "report";

  public static final String FileName_Log4jProperty = "log4j.properties";
  public static final String FileName_DefaultProperty = "guitester.properties";

  public static final String FileSeperator = System.getProperty("file.separator");
  public static final String PathSeperator = System.getProperty("path.separator");
  public static final String CoverageFileName = "coberturacov.ser";
  public static final String CleanCoverageFileName = "cleanCoverage.ser";

  @Option(name = "-platform", usage = "UI Platform")
  private static String UIPlatform;

  @Option(name = "-main", usage = "main class name")
  private static String mainClassName;

  @Option(name = "-gui", usage = "boolean value for using GUI dialog")
  private static Boolean useGUI;

  @Option(name = "-interactive", usage = "execute GUI Tester as a interactive mode")
  private static Boolean isInteractiveMode;

  @Option(name = "-verbose", usage = "print detailed messages")
  private static Boolean verbose;

  @Option(name = "-debug", usage = "run as debug mode")
  private static Boolean debug;

  @Option(name = "-plugin_class_folder", usage = "a path to the plugin folder class. ex].")
  private static String pluginClassFolder;

  @Option(name = "-project_folder", usage = "a project folder")
  private static String projDir;

  @Option(name = "-result_folder", usage = "a folder of the result")
  private static String resultDir;

  @Option(name = "-workspace_index", usage = "the index of workspace")
  private static Integer workspaceIndex;

  @Option(name = "-strategy", usage = "a name of strategy")
  private static String strategyName;

  @Option(name = "-strategy_args", usage = "arguments of strategy. ex] (ArgKey1)=(ArgValue1)[:(ArgKey2)=(ArgValue2)]")
  private static String strategyArgs;

  @Option(name = "-event_handler_list", usage = "a list of event handlers ex] ActionableEvent;SelectionTableEvent")
  private static String eventHandlers;

  @Option(name = "-app_manager", usage = "a class name of application manager that will replace built-in application manager.")
  private static String pluginAppManager;

  @Option(name = "-delay_manager", usage = "a class name of delay manager that will replace built-in delay manager.")
  private static String pluginDelayManager;

  @Option(name = "-window_monitor", usage = "a class name of window monitor that will replace built-in window monitor.")
  private static String pluginWindowMonitor;

  @Option(name = "-gui_model_extractor", usage = "a class name of GUI model extractor that will replace built-in GUI model extractor.")
  private static String pluginGUIModelExtractor;

  @Option(name = "-id_generator", usage = "a class name of ID generator that will replace built-in ID generator.")
  private static String pluginIDGenerator;

  @Option(name = "-component_notifier", usage = "a class name of component notifier that will replace built-in ID generator.")
  private static String pluginComponentNotifier;

  @Option(name = "-init_delay", usage = "an initial delay of GUITester[in milliseconds]")
  private static Integer initialDelay;

  @Option(name = "-execution_delay", usage = "an execution delay of GUITester[in milliseconds]")
  private static Integer executionDelay;

  @Option(name = "-componentfilter", usage = "the file name of event filter")
  private static String componentFilterFile;

  @Option(name = "-eventvalue", usage = "the file name of custom event value")
  private static String customValueFile;

  @Argument
  private static List<String> arguments = new ArrayList<String>();

  public static KeyValueStore propertyStore = new KeyValueStore();

  public static ApplicationClassLoader appClassLoader = null;
  public static PluginClassLoader pluginClassLoader = null;

  public static EventValueManager eventValueManager = null;

  public static EventFilter eventFilter = new EventFilter();

  public final static XStream xStream = new XStream(new Xpp3Driver());

  // new DomDriver("UTF-8"));
  // To use Xpp3, 'new Xpp3Driver()'. else new DomDriver("UTF-8")'

  // ##static methods##
  public static void setDefaultLog4jProperties() {
    // set log4j
    File log4jPropertyFile = getFile("guitester.log4j_properties");
    if (log4jPropertyFile != null && log4jPropertyFile.exists()) {
      PropertyConfigurator.configure(log4jPropertyFile.getAbsolutePath());
    }
  }

  @Deprecated
  public static void setBackToPropertyFields() {
    // set back to fields in TestProperty before overwritten by arguments parser.
    UIPlatform = propertyStore.get("guitester.platform", "JFC");
    mainClassName = propertyStore.get("guitester.main", "");
    useGUI = Boolean.valueOf(propertyStore.get("guitester.gui", "false"));
    isInteractiveMode = Boolean.valueOf(propertyStore.get("guitester.interactive", "false"));
    verbose = Boolean.valueOf(propertyStore.get("guitester.verbose", "false"));
    debug = Boolean.valueOf(propertyStore.get("guitester.debug", "false"));
    projDir = propertyStore.get("guitester.project_folder", "");
    workspaceIndex = Integer.parseInt(propertyStore.get("guitester.workspace_index", "0"));
    resultDir = propertyStore.get("guitester.result_folder", "");
    strategyName = propertyStore.get("guitester.strategy", "");
    strategyArgs = propertyStore.get("guitester.strategy_args", "");
    eventHandlers = propertyStore.get("plugins.event_handler_list",
        "ActionableEvent;EditableTextEvent;SelectionEvent;SelectionTableEvent;SelectionMenuEvent;ValueEvent");
    pluginAppManager = propertyStore.get("plugins.app_manager", "");
    pluginDelayManager = propertyStore.get("plugins.delay_manager", "");
    pluginWindowMonitor = propertyStore.get("plugins.window_monitor", "");
    pluginGUIModelExtractor = propertyStore.get("plugins.gui_model_extractor", "");
    pluginIDGenerator = propertyStore.get("plugins.id_generator", "");
    pluginComponentNotifier = propertyStore.get("plugins.component_notifier", "");
    initialDelay = Integer.parseInt(propertyStore.get("delay_manager.init_delay", "2000"));
    executionDelay = Integer.parseInt(propertyStore.get("delay_manager.execution_delay", "300"));
    componentFilterFile = propertyStore.get("gui_model_extractor.component_filter_filename", "eventFilter.filter");
    customValueFile = propertyStore.get("gui_model_extractor.event_value_filename", "eventValue.value");

    String argsStr = propertyStore.get("guitester.main_args", "");
    String[] argList = argsStr.split(" ");
    arguments.clear();
    for (String arg : argList) {
      if (!arg.trim().equals("")) {
        arguments.add(arg);
      }
    }
  }

  public static void initialize() {
    // set the locale to English
    System.setProperty("apple.awt.fileDialogForDirectories", "true");
    System.setProperty("user.language", "en");
    System.setProperty("user.country", "US");
    System.setProperty("user.variant", "US");
    Locale locale = Locale.US;
    Locale.setDefault(locale);
    TestLogger.debug("Initial DisplayLanguage is " + Locale.getDefault().getDisplayLanguage());
    TestLogger.debug("Environment: user.home=%s  user.dir=%s", System.getProperty("user.home"),
        System.getProperty("user.home"));
    // set cross platform look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    initializeProperties();

    // setBackToPropertyFields();

    // construct GUITester
    GUITesterBuilder builder = new GUITesterBuilder(propertyStore.get("guitester.platform"));
    builder.construct();

    // initialize AUT
    initializeAUT();
    // initialize plugins, events, and so on.
    initializePlugIns();
    initializeEvents();
    initializeFilter();
    initializeValueManager();

  }

  public static Properties getTestPropertyFile(URL file) {
    if (file == null)
      return null;
    Properties result = new Properties();

    // try to load default test properties from file.
    InputStream property = null;
    try {
      property = file.openStream();
      result.load(property);
      TestLogger.warn("\tProperty file is loaded from %s", file.toExternalForm());
    } catch (FileNotFoundException e) {
      TestLogger.warn("\tThere is no test property file in %s", file.toExternalForm());
    } catch (IOException e) {
      TestLogger.error("Error occured during the test property loading from %s", file.toExternalForm());
    } finally {
      if (property != null) {
        try {
          property.close();
        } catch (IOException e) {
        }
      }
    }
    return result;
  }

  public static void initializeProperties() {
    // load default test properties from file.
    File defaultFile = getFile("guitester.default_properties");
    Properties defaultProperties = null;
    if (defaultFile.exists()) {
      try {
        defaultProperties = getTestPropertyFile(defaultFile.toURI().toURL());
      } catch (MalformedURLException e) {
      }
    } else {
      defaultProperties = getTestPropertyFile(Main.class.getResource("/" + FileName_DefaultProperty));
    }

    if (defaultProperties != null) {
      propertyStore.putAll(defaultProperties);
      propertyStore.setArgs(propertyStore.get("guitester.strategy_args", ""));
    }

    // first, load project path from command line arguments
    if (projDir != null)
      propertyStore.put("guitester.project_folder", projDir);

    // then, rewrite test properties with project test properties
    Properties projectProperties = null;
    try {
      projectProperties = getTestPropertyFile(getFile("project.default_properties").toURI().toURL());
    } catch (MalformedURLException e) {
    }
    if (projectProperties != null) {
      propertyStore.putAll(projectProperties);
      propertyStore.setArgs(propertyStore.get("guitester.strategy_args", ""));
    }

    // overwrite test properties with command line arguments
    if (UIPlatform != null)
      propertyStore.put("guitester.platform", UIPlatform);
    if (mainClassName != null)
      propertyStore.put("guitester.main", mainClassName);
    if (useGUI != null)
      propertyStore.put("guitester.gui", useGUI ? "true" : "false");
    if (isInteractiveMode != null)
      propertyStore.put("guitester.interactive", isInteractiveMode ? "true" : "false");
    if (verbose != null)
      propertyStore.put("guitester.verbose", verbose ? "true" : "false");
    if (debug != null)
      propertyStore.put("guitester.debug", debug ? "true" : "false");
    if (projDir != null)
      propertyStore.put("guitester.project_folder", projDir);
    if (workspaceIndex != null)
      propertyStore.put("guitester.workspace_index", String.valueOf(workspaceIndex));
    if (resultDir != null)
      propertyStore.put("guitester.result_folder", resultDir);
    if (strategyName != null)
      propertyStore.put("guitester.strategy", strategyName);
    if (strategyArgs != null)
      propertyStore.put("guitester.strategy_args", strategyArgs);

    if (eventHandlers != null)
      propertyStore.put("plugins.event_handler_list", eventHandlers);
    if (pluginAppManager != null)
      propertyStore.put("plugins.app_manager", pluginAppManager);
    if (pluginDelayManager != null)
      propertyStore.put("plugins.delay_manager", pluginDelayManager);
    if (pluginWindowMonitor != null)
      propertyStore.put("plugins.window_monitor", pluginWindowMonitor);
    if (pluginGUIModelExtractor != null)
      propertyStore.put("plugins.gui_model_extractor", pluginGUIModelExtractor);
    if (pluginIDGenerator != null)
      propertyStore.put("plugins.id_generator", pluginIDGenerator);
    if (pluginComponentNotifier != null)
      propertyStore.put("plugins.component_notifier", pluginComponentNotifier);

    if (initialDelay != null)
      propertyStore.put("delay_manager.init_delay", String.valueOf(initialDelay));
    if (executionDelay != null)
      propertyStore.put("delay_manager.execution_delay", String.valueOf(executionDelay));

    if (componentFilterFile != null)
      propertyStore.put("gui_model_extractor.component_filter_filename", componentFilterFile);
    if (customValueFile != null)
      propertyStore.put("gui_model_extractor.event_value_filename", customValueFile);

    if (arguments != null && arguments.size() > 0)
      propertyStore.put("guitester.main_args", Joiner.on(" ").join(arguments));

    // overwrite test properties with strategy arguments
    propertyStore.setArgs(propertyStore.get("guitester.strategy_args", ""));

    // reload log4j configuration for given project folder
    File log4jPropertyFile = getFile("project.log4j_properties");
    if (log4jPropertyFile != null && log4jPropertyFile.exists()) {
      PropertyConfigurator.configure(log4jPropertyFile.getAbsolutePath());
      TestLogger.debug("log4j configuration file is reloaded from %s", log4jPropertyFile.getAbsolutePath());
    }

    TestLogger.debug("Test properties:  %s", propertyStore.toString());

  }

  private static void initializeAUT() {
    TestLogger.debug("Initialize AUT:");
    appClassLoader = new ApplicationClassLoader(Thread.currentThread().getContextClassLoader());

    // add plugins folder
    appClassLoader.addClassPath(getFolder("instrumented")); // instrumented folder
    appClassLoader.addClassPath(getFolder("binary")); // binary folder
    appClassLoader.addClassFolder(getFolder("library")); // binary folder
    appClassLoader.addClassFolder(getFolder("guitester.aut_ext_lib")); // AUT's external lib
    TestLogger
        .debug("\t%d class paths are loaded for application class loader.", appClassLoader.getAddedPaths().size());
    TestLogger.debug("");
  }

  private static void initializePlugIns() {

    TestLogger.debug("Initialize plugins:");
    TestLogger.debug("\tPlugin folder: %s", getFolder("guitester.plugins"));
    ClassLoader parentClassLoader = appClassLoader != null ? appClassLoader : Thread.currentThread()
        .getContextClassLoader();
    pluginClassLoader = new PluginClassLoader(parentClassLoader);
    // Thread.currentThread().setContextClassLoader(pluginClassLoader);

    // add plugins folder
    pluginClassLoader.addPluginFolder(getFolder("plugins")); // project-specific plugins
    pluginClassLoader.addPluginFolder(getFolder("guitester.plugins")); // guitester's plugins
    TestLogger.debug("");

    GUITester guiTester = GUITester.getInstance();

    // set main plugins if possible
    for (String className : pluginClassLoader.getAddedPlugins()) {
      try {
        Class<?> cl = Class.forName(className, true, pluginClassLoader);

        if (AbstractStrategy.class.isAssignableFrom(cl)) { // if Strategy plugin
          StrategyManager.getInstance().registerStrategyClass(cl.getName(), (Class<? extends AbstractStrategy>) cl);
        } else if (TestProperty.propertyStore.get("plugins.app_manager").equals(className)
            && ApplicationManager.class.isAssignableFrom(cl)) {
          guiTester.setApplicationManager((ApplicationManager) cl.newInstance());
        } else if (TestProperty.propertyStore.get("plugins.delay_manager").equals(className)) {
          if (DelayManager.class.isAssignableFrom(cl))
            guiTester.setDelayManager((DelayManager) cl.newInstance());
        } else if (TestProperty.propertyStore.get("plugins.window_monitor").equals(className)
            && WindowMonitor.class.isAssignableFrom(cl)) {
          guiTester.setWindowMonitor((WindowMonitor) cl.newInstance());
        } else if (TestProperty.propertyStore.get("plugins.gui_model_extractor").equals(className)
            && GUIModelExtractor.class.isAssignableFrom(cl)) {
          guiTester.setGuiModelExtractor((GUIModelExtractor) cl.newInstance());
        } else if (TestProperty.propertyStore.get("plugins.id_generator").equals(className)
            && IDGenerator.class.isAssignableFrom(cl)) {
          guiTester.setIdGenerator((IDGenerator) cl.newInstance());
        } else if (TestProperty.propertyStore.get("plugins.component_notifier").equals(className)
            && ComponentNotifier.class.isAssignableFrom(cl)) {
          guiTester.setComponentNotifier((ComponentNotifier) cl.newInstance());
        }

      } catch (Throwable e) {
        TestLogger.error(e.getMessage());
        e.printStackTrace();
      }
    }

    // check main plugins' existance
    if (!TestProperty.propertyStore.get("plugins.app_manager").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.app_manager"))) {
      TestLogger.warn("\tPlugin file for 'plugins.app_manager' doesn't exist!");
    }
    if (!TestProperty.propertyStore.get("plugins.delay_manager").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.delay_manager"))) {
      TestLogger.warn("\tPlugin file for 'plugins.delay_manager' doesn't exist!");
    }
    if (!TestProperty.propertyStore.get("plugins.window_monitor").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.window_monitor"))) {
      TestLogger.warn("\tPlugin file for 'plugins.window_monitor' doesn't exist!");
    }
    if (!TestProperty.propertyStore.get("plugins.gui_model_extractor").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.gui_model_extractor"))) {
      TestLogger.warn("\tPlugin file for 'plugins.gui_model_extractor' doesn't exist!");
    }
    if (!TestProperty.propertyStore.get("plugins.id_generator").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.id_generator"))) {
      TestLogger.warn("\tPlugin file for 'plugins.id_generator' doesn't exist!");
    }
    if (!TestProperty.propertyStore.get("plugins.component_notifier").equals("")
        && !pluginClassLoader.containsClass(TestProperty.propertyStore.get("plugins.component_notifier"))) {
      TestLogger.warn("\tPlugin file for 'plugins.component_notifier' doesn't exist!");
    }

  }

  public static void initializeEvents() {

    EventManager.getInstance().registerEventHandlerLists(TestProperty.propertyStore.get("plugins.event_handler_list"));

    // XStream initialization
    xStream.registerConverter(new GUIModel(true));
    xStream.registerConverter(new ComponentModel());
    xStream.registerConverter(new EventListModel());
    xStream.alias("gui", GUIModel.class);
    xStream.alias("window", ComponentModel.class);
    xStream.alias("component", ComponentModel.class);
    xStream.alias("eventlist", EventListModel.class);
    for (Class<? extends EventModel> handlerClass : EventManager.getInstance().getEventHandlerClassesList()) {
      try {
        EventModel eventChecker = EventManager.getInstance().getEventFromPool(handlerClass);
        xStream.registerConverter(eventChecker);
        xStream.alias("event", eventChecker.getClass());
      } catch (Throwable t) {
        TestLogger.log.error("ERR: Event converter registration error!", t);
      }
    }
    xStream.autodetectAnnotations(true);
  }

  public static void initializeFilter() {
    File filterFile = getFile("project.component_filter_file");
    if (filterFile.exists()) {
      EventListModel filterList = (EventListModel) IOUtil.loadObjectFromXML(filterFile.getAbsolutePath());
      eventFilter.setFilter(filterList);
    }
  }

  public static void initializeValueManager() {

    File customValueFile = getFile("project.event_value_file");
    if (customValueFile.exists()) {
      EventListModel customValueList = (EventListModel) IOUtil.loadObjectFromXML(customValueFile.getAbsolutePath());
      eventValueManager = new EventValueManager(customValueList);
    } else {
      eventValueManager = new EventValueManager(new EventListModel());
    }

  }

  /*
   * need to consider <i>TestProeprty.projDir</i>
   */
  public static List<String> getClassPathList(boolean includeInstrumentFolder) {
    List<String> result = new ArrayList<String>();
    String classPath = getClassPath(includeInstrumentFolder);
    for (String classPathItem : classPath.split(TestProperty.PathSeperator)) {
      result.add(classPathItem);
    }
    return result;
  }

  public static String getClassPath(boolean includeInstrumentFolder) {
    String classPath;
    if (includeInstrumentFolder) {
      String instrumentedPath = getFolder("instrumented").getAbsolutePath();
      classPath = instrumentedPath + TestProperty.PathSeperator + getFolder("bin").getAbsolutePath()
          + TestProperty.PathSeperator + System.getProperties().get("java.class.path") + TestProperty.PathSeperator;
    } else
      classPath = getFolder("bin").getAbsolutePath() + TestProperty.PathSeperator
          + System.getProperties().get("java.class.path") + TestProperty.PathSeperator;

    return classPath;
  }

  public static String getJavaPath() {
    String OSStr = System.getProperty("os.name");
    boolean isWindow = OSStr.toLowerCase().indexOf("windows") >= 0;
    if (isWindow)
      return System.getProperty("java.home") + TestProperty.FileSeperator + "bin" + TestProperty.FileSeperator
          + "java.exe";
    else
      return System.getProperty("java.home") + TestProperty.FileSeperator + "bin" + TestProperty.FileSeperator + "java";
  }

  public static File getFolder(String name) {
    name = name.toLowerCase();
    if ("guitester".equals(name)) {
      final File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
      if (f.exists()) {
        String guiTesterPath = f.getAbsolutePath();
        if (guiTesterPath.endsWith("bin")) {
          guiTesterPath = guiTesterPath.substring(0, guiTesterPath.length() - 4);
        } else if (guiTesterPath.replace('\\', '/').endsWith("guitesting/main")) {
          guiTesterPath = guiTesterPath.substring(0, guiTesterPath.length() - 16);
        } else if (guiTesterPath.endsWith(".jar")){
          guiTesterPath = guiTesterPath.replace('\\', '/').substring(0,guiTesterPath.lastIndexOf("/"));
        }
        return new File(guiTesterPath);
      }

    } else if ("guitester.plugins".equals(name)) {
      return new File(getFolder("guitester"), FolderName_PlugIns);
    } else if ("guitester.aut_ext_lib".equals(name)) {
      return new File(getFolder("guitester"), FolderName_AUTExtLibraries);
    } else if ("project".equals(name)) {
      if (!propertyStore.get("guitester.project_folder", "").equals("")) {
        return new File(propertyStore.get("guitester.project_folder"));
      }
    } else if ("source".equals(name)) {
      return new File(getFolder("project"), FolderName_Source);
    } else if ("binary".equals(name)) {
      return new File(getFolder("project"), FolderName_Binary);
    } else if ("config".equals(name)) {
      return new File(getFolder("project"), FolderName_Configuration);
    } else if ("instrumented".equals(name)) {
      return new File(getFolder("project"), FolderName_Instrumented);
    } else if ("library".equals(name)) {
      return new File(getFolder("project"), FolderName_Libraries);
    } else if ("plugins".equals(name)) {
      return new File(getFolder("project"), FolderName_PlugIns);
    } else if ("aut_ext_lib".equals(name)) {
      return new File(getFolder("project"), FolderName_AUTExtLibraries);
    } else if ("testsuite".equals(name)) {
      return new File(getFolder("project"), FolderName_TestSuite);
    } else if ("workspace".equals(name)) {
      return new File(getFolder("project"), FolderName_Workspace + propertyStore.getInt("guitester.workspace_index"));
    } else if ("result".equals(name)) {
      if (!propertyStore.get("guitester.result_folder", "").equals("")) {
        return new File(propertyStore.get("guitester.result_folder"));
      } else
        return new File(getFolder("workspace"), FolderName_Result);
    } else if ("input".equals(name)) {
      return new File(getFolder("workspace"), FolderName_InputFile);
    } else if ("output".equals(name)) {
      return new File(getFolder("workspace"), FolderName_OutputFile);
    } else if ("error".equals(name)) {
      return new File(getFolder("result"), FolderName_Error);
    } else if ("coverage".equals(name)) {
      return new File(getFolder("result"), FolderName_Coverage);
    } else if ("metadata".equals(name)) {
      return new File(getFolder("result"), FolderName_MetaData);
    } else if ("report".equals(name)) {
      return new File(getFolder("result"), FolderName_Report);
    } else if ("trace".equals(name)) {
      return new File(getFolder("result"), FolderName_Trace);
    } else if ("img.window".equals(name)) {
      return new File(getFolder("trace"), FolderName_WindowImage);
    } else if ("img.component".equals(name)) {
      return new File(getFolder("trace"), FolderName_ComponentImage);
    }
    throw new RuntimeException("No proper argument in getFolder(String) for '" + name + "'");

  }

  public static File getFile(String name) {
    name = name.toLowerCase();
    if ("guitester.log4j_properties".equals(name)) {
      return new File(getFolder("guitester"), FileName_Log4jProperty);
    } else if ("guitester.default_properties".equals(name)) {
      return new File(getFolder("guitester"), FileName_DefaultProperty);
    } else if ("project.default_properties".equals(name)) {
      return new File(getFolder("project"), FileName_DefaultProperty);
    } else if ("project.clean_coverage".equals(name)) {
      return new File(getFolder("project"), CleanCoverageFileName);
    } else if ("project.log4j_properties".equals(name)) {
      return new File(getFolder("project"), FileName_Log4jProperty);
    } else if ("project.component_filter_file".equals(name)) {
      return new File(getFolder("project"), propertyStore.get("gui_model_extractor.component_filter_filename",
          "eventFilter.filter"));
    } else if ("project.event_value_file".equals(name)) {
      return new File(getFolder("project"), propertyStore.get("gui_model_extractor.event_value_filename",
          "eventValue.value"));
    }

    throw new RuntimeException("No proper argument in getFile(String) for '" + name + "'");

  }
}
