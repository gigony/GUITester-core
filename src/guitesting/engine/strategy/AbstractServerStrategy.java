package guitesting.engine.strategy;

import guitesting.model.ipc.RemoteDataCall;
import guitesting.util.IOUtil;
import guitesting.util.KeyValueStore;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Joiner;

public abstract class AbstractServerStrategy extends AbstractStrategy {

  protected RemoteDataCall serverObject;
  protected String RMIServerURL;

  protected abstract void init(String[] mainArgs);

  protected abstract RemoteDataCall createRemoteDataCallObject() throws RemoteException;

  protected abstract void execute(String[] mainArgs);

  @Override
  public void run(String[] mainArgs) {
    init(mainArgs);

    startRMIServer();

    execute(mainArgs);

    shutdownRMIServer();
  }

  protected void startRMIServer() {
    try {
      serverObject = createRemoteDataCallObject();

      String mainName = TestProperty.propertyStore.get("guitester.main");
      // get simple name
      mainName = mainName.substring(mainName.lastIndexOf(".") + 1, mainName.length());
      String strategyName = this.getClass().getSimpleName();
      int portNum = TestProperty.propertyStore.getInt("guitester.rmi.port_num", 10001);

      RMIServerURL = String.format("rmi://127.0.0.1:%d/RemoteDataCallImpl_%s_%s", portNum, mainName, strategyName);

      Naming.rebind(RMIServerURL, serverObject);

      // set server strategy object.
      serverObject.setServerObj(this);

    } catch (Exception e) {
      e.printStackTrace();
      TestLogger.error("Failed to launch RMI Server.");
      System.exit(1);
    }
    TestLogger.info("RMI Server is ready.");

  }

  protected void shutdownRMIServer() {
    try {
      Naming.unbind(RMIServerURL);
      UnicastRemoteObject.unexportObject(serverObject, true);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void forkClient() throws TimeoutException {
    forkClient("");
  }

  public void forkClient(String strategyArgs) throws TimeoutException {
    List<String> command = new ArrayList<String>();

    String javaPath = TestProperty.getJavaPath();
    List<String> jvmArgList = IOUtil.getJVMArguments();
    String classPath = System.getProperty("java.class.path");
    String workspacePath = TestProperty.getFolder("workspace").getAbsolutePath();

    // append arguments except strategy name and strategy_args
    ArrayList<String> arguments = new ArrayList<String>();

    String[] originalArgs = TestProperty.getOriginalArgs();
    int originalArgsLen = originalArgs.length;
    for (int i = 0; i < originalArgsLen; i++) {
      if (originalArgs[i].equals("-strategy") || originalArgs[i].equals("-strategy_args")) {
        i++; // skip this option and argument.
      } else {
        arguments.add(originalArgs[i]);
      }
    }

    String newStrategyName = TestProperty.propertyStore.get("guitester.strategy");

    // append "_Client" for identifying client strategy name
    if (!newStrategyName.equals("")) {
      newStrategyName = newStrategyName + "_Client";
    }

    // set new strategy args
    String newStrategyArgs = TestProperty.propertyStore.get("guitester.strategy_args");
    if (strategyArgs != null) {
      strategyArgs = strategyArgs.trim();
      if (!newStrategyArgs.equals("")) { // append to the previous strategy args
        newStrategyArgs = newStrategyArgs + "," + strategyArgs;
      } else {
        newStrategyArgs = strategyArgs;
      }
    }

    // substitute jvm argument if "java.vm_args" property is set
    if (!TestProperty.propertyStore.get("java.vm_args").equals("")) {
      jvmArgList = new ArrayList<String>();

      for (String arg : TestProperty.propertyStore.get("java.vm_args").split(" ")) {
        jvmArgList.add(arg);
      }
      // append "user.home" and "user.dir" property
      jvmArgList.add("-Duser.home=" + TestProperty.getFolder("workspace"));
      jvmArgList.add("-Duser.dir=" + TestProperty.getFolder("workspace"));
    }

    command.add(javaPath);
    command.addAll(jvmArgList);
    command.add("-cp");
    command.add(classPath);
    command.add("guitesting.main.Main");
    command.add("-strategy");
    command.add(newStrategyName);
    if (!newStrategyArgs.equals("")) {
      command.add("-strategy_args");
      // normalize arguments
      KeyValueStore tempStore = new KeyValueStore(newStrategyArgs);
      command.add(tempStore.toString());
    }
    command.addAll(arguments);

    String commandString = Joiner.on(" ").join(command);

    TestLogger.debug("Launch client: %s", newStrategyName);
    TestLogger.debug("\tWorkspace: %s", workspacePath);
    TestLogger.debug("\tStrategy args: %s", newStrategyArgs);
    TestLogger.debug("\tCommand: %s", commandString);

    IOUtil.executeCommand(command, workspacePath, TestProperty.propertyStore.getInt("client.time_out", 0));

  }

}
