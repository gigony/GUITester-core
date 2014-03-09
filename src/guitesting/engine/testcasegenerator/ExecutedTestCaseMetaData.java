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
package guitesting.engine.testcasegenerator;

import java.io.Serializable;

public class ExecutedTestCaseMetaData implements Serializable {
  private static final long serialVersionUID = 1L;

  int testCaseIndex = 0;
  int initTimeDelay = 0;
  int executionTimeDelay = 0;
  long elapsedTime = 0;
  int unexecutableIndex = 0;
  int failureIndex = 0;
  int intendedLength = 0;
  int executedLength = 0;

  public ExecutedTestCaseMetaData() {

  }

  public int getTestCaseIndex() {
    return testCaseIndex;
  }

  public int getInitTimeDelay() {
    return initTimeDelay;
  }

  public int getExecutionTimeDelay() {
    return executionTimeDelay;
  }

  public long getElapsedTime() {
    return elapsedTime;
  }

  public int getUnexecutableIndex() {
    return unexecutableIndex;
  }

  public int getFailureIndex() {
    return failureIndex;
  }

  public int getLength() {
    return intendedLength;
  }

  public int getExecutedLength() {
    return executedLength;
  }

  public void setTestCaseIndex(int testCaseIndex) {
    this.testCaseIndex = testCaseIndex;
  }

  public void setInitTimeDelay(int initTimeDelay) {
    this.initTimeDelay = initTimeDelay;
  }

  public void setExecutionTimeDelay(int executionTimeDelay) {
    this.executionTimeDelay = executionTimeDelay;
  }

  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public void setUnexecutableIndex(int unexecutableIndex) {
    this.unexecutableIndex = unexecutableIndex;
  }

  public void setFailureIndex(int failureIndex) {
    this.failureIndex = failureIndex;
  }

  public void setLength(int intendedLength) {
    this.intendedLength = intendedLength;
  }

  public void setExecutedLength(int executedLength) {
    this.executedLength = executedLength;
  }

  public void print() {
    System.out.println();
    System.out.println("== Test case index: " + testCaseIndex);
    System.out.println("\tintended length:" + intendedLength);
    System.out.println("\texecuted length:" + executedLength);
    System.out.println("\tinit time delay:" + initTimeDelay);
    System.out.println("\texecution time delay:" + executionTimeDelay);
    System.out.println("\tunexecutable index:" + unexecutableIndex);
    System.out.println("\tfailure index:" + failureIndex);
    System.out.println("\telapsed time:" + elapsedTime);
    System.out.println("==========================");
    System.out.println();
  }

  public String printString() {
  	String result=
  	"\n== Test case index: "+testCaseIndex+
  	"\n\tintended length:"+intendedLength+
  	"\n\texecuted length:"+executedLength+
  	"\n\tinit time delay:"+initTimeDelay+
  	"\n\texecution time delay:"+executionTimeDelay+
  	"\n\tunexecutable index:"+unexecutableIndex+
  	"\n\tfailure index:"+failureIndex+
  	"\n\telapsed time:"+elapsedTime+
  	"\n=========================="+
  	"\n";
    return result;
  }

}
