package guitesting.engine.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.util.Set;

public interface ExtendedClassFileTransformer extends ClassFileTransformer {
  void setAnalysisScope(Set<String> analysisScope);

  void setVariable(String varName, Object value);
}
