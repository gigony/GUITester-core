package guitesting.util;

import java.security.Permission;

public class MySecurityManager {
  private static SecurityManager origSecurityManager = System.getSecurityManager();

  private Runnable runnableObj = null;
  private SecurityManager oldSecurityManager = null;

  public class ExitCaughtedException extends SecurityException {
    private static final long serialVersionUID = 1L;

    public ExitCaughtedException(Runnable runnable) {
      if (runnable != null) {
        runnable.run();
      }
    }
  }

  public void setRunnableObj(Runnable runnableObj) {
    this.runnableObj = runnableObj;
  }

  public Runnable getRunnableObj() {
    return runnableObj;
  }

  public void disableExitMethod(boolean b) {
    if (oldSecurityManager == null)
      oldSecurityManager = origSecurityManager;

    if (b) {
      final SecurityManager securityManager = new SecurityManager() {
        @Override
        public void checkPermission(Permission permission, Object context) {
          if ("exitVM".equals(permission.getName())) {
            throw new ExitCaughtedException(runnableObj);
          }
        }

        @Override
        public void checkPermission(Permission permission) {
          if ("exitVM".equals(permission.getName())) {
            throw new ExitCaughtedException(runnableObj);
          }
        }
      };
      System.setSecurityManager(securityManager);
    } else {
      System.setSecurityManager(origSecurityManager);
    }

  }

}
