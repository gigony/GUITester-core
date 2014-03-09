package guitesting.util;

import java.util.ArrayList;

import org.junit.Test;

public class IOUtilTest {

  @Test
  public void test() {
    ArrayList<Double> temp = new ArrayList<Double>();
    temp.add(0.2);
    temp.add(0.4);
    temp.add(0.6);
    temp.add(0.8);
    temp.add(1.0);
    temp.add(6.0);

    int[] count = new int[6];
    IOUtil.findClosestWeight(temp, 0, 5, 0.43887);
    for (int i = 0; i < 10000; i++) {
      double rndValue = IOUtil.getRandomDouble() * 6.0;
      count[IOUtil.findClosestWeight(temp, 0, 5, rndValue)]++;
      // System.out.print(rndValue);
      // System.out.println("==> "+findClosestWeight(temp,0,5,rndValue));
    }
    for (int i = 0; i < 6; i++) {
      System.out.println(count[i]);
    }
  }

}
