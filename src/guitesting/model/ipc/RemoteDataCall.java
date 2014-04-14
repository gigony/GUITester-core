package guitesting.model.ipc;

import guitesting.engine.strategy.AbstractServerStrategy;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDataCall extends Remote {
  
  public void setServerObj(AbstractServerStrategy obj) throws RemoteException;
  
  public void pushData(String key, Object data) throws RemoteException;  

  public Object getData(String key,Object data) throws RemoteException;
}