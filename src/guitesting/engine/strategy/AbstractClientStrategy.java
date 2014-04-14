package guitesting.engine.strategy;

import guitesting.model.ipc.RemoteDataCall;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.rmi.Naming;
import java.rmi.RemoteException;

public abstract class AbstractClientStrategy extends AbstractStrategy {

  protected RemoteDataCall serverObject;

  protected String RMIServerURL;

  protected abstract void init(String[] mainArgs);

  protected abstract void execute(String[] mainArgs) throws RemoteException;

  @Override
  public void run(String[] mainArgs) {
    init(mainArgs);

    connectRMIServer();

    try {

      execute(mainArgs);

    } catch (RemoteException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  protected void connectRMIServer() {
    try {
      String mainName = TestProperty.propertyStore.get("guitester.main");
      // get simple name
      mainName = mainName.substring(mainName.lastIndexOf(".") + 1, mainName.length());
      String strategyName = this.getClass().getSimpleName();

      // remove postfix to indicate a server class name
      strategyName = strategyName.substring(0, strategyName.lastIndexOf("_Client"));

      int portNum = TestProperty.propertyStore.getInt("guitester.rmi.port_num", 10001);

      RMIServerURL = String.format("rmi://127.0.0.1:%d/RemoteDataCallImpl_%s_%s", portNum, mainName, strategyName);

      serverObject = (RemoteDataCall) Naming.lookup(RMIServerURL);

    } catch (Exception e) {
      e.printStackTrace();
      TestLogger.error("Failed to connect to RMI Server (%s).", RMIServerURL);
      System.exit(1);
    }
    TestLogger.info("Connected to RMI Server (%s).", RMIServerURL);

  }

}
