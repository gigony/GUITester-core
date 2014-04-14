package guitesting.engine.instrumentation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class InstrumentPointSet implements Serializable {
  private static final long serialVersionUID = 9218953380022446060L;

  Set<String> classInstPoints = new HashSet<String>();
  Set<String> methodInstPoints = new HashSet<String>();
  Set<String> guiAccessorInstPoints = new HashSet<String>();
  Set<String> guiGetterSet = new HashSet<String>();
  Set<String> guiSetterSet = new HashSet<String>();

  public InstrumentPointSet() {
  }

  public void addPoint(String className, String methodName, String methodSignature, int accessType) {
    classInstPoints.add(className);
    methodInstPoints.add(methodName);
    guiAccessorInstPoints.add(methodSignature);
    switch (accessType) {
    case 1://MethodMatcher.GETTER:
      guiGetterSet.add(methodSignature);
      break;
    case 2://MethodMatcher.SETTER:
      guiSetterSet.add(methodSignature);
      break;
    }
  }

  public boolean isInAnalysisScope(String className, String methodName, String methodSignature) {
    if (className == null || classInstPoints.contains(className)) {
      if (methodName == null || methodInstPoints.contains(methodName))
        if (methodSignature == null || guiAccessorInstPoints.contains(methodSignature)) {
          return true;
        }
    }
    return false;
  }

  public int getAccessType(String methodSignature) {
    if (!guiAccessorInstPoints.contains(methodSignature))
      return 0;//MethodMatcher.UNKNOWN;

    if (guiGetterSet.contains(methodSignature))
      return 1;//MethodMatcher.GETTER;
    else
      return 2;//MethodMatcher.SETTER;
  }
  
  public void dump(){
    for(String point:methodInstPoints){
      System.out.println(point);
    }
  }

}
