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
package guitesting.model.event;

import guitesting.model.ComponentModel;
import guitesting.util.JFCUtil;

import javax.accessibility.AccessibleTable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
public class SelectionTableEvent_JFC extends EventModel {

  public SelectionTableEvent_JFC(ComponentModel model) {
    super(model);
  }

  @Override
  public void performImpl(Object... args) {
    // TODO implement this
    // AccessibleTable aTable = getAccessibleContext().getAccessibleTable();

  }

  @Override
  public String getEventTypeName() {
    return "selection table event";
  }
  @Override
  public int getValueHash() {
    return 0;
  }

  @Override
  public boolean isSupportedBy(ComponentModel model) {

    AccessibleTable aTable = JFCUtil.getAccessibleContext(model).getAccessibleTable();
    if (aTable == null)
      return false;

    return false;
  }

  @Override
  public boolean stopChainingIfSupported() {
    return false;
  }

  @Override
  public boolean canConvert(Class clazz) {
    return SelectionTableEvent_JFC.class.equals(clazz);
  }

}
