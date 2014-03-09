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
package guitesting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/*
 * This class is used for both TableModel and PropertyModel for event.
 * The information of component property will be stored in event.getComponentModel().map if eventModel is used, and use the method 'getRefMapPropertyKey(String)'if you want to retrieve info from TableModel in propertyPane  
 */
@SuppressWarnings("serial")
public class ValuePropertyModel extends PropertyModel {
  protected ArrayList<String> keyList = new ArrayList<String>();

  public void addRow() {
    String value = map.get("");
    if (value == null) {
      keyList.add(0, "");
      map.put("", "");
      fireTableRowsInserted(0, 0);
    }
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  @Override
  public void setproperty(Map<String, String> properties) {
    int size = size();
    map.clear();
    keyList.clear();
    if (size > 0)
      fireTableRowsDeleted(0, size - 1);
    map.putAll(properties);
    keyList.addAll(map.keySet());
    Collections.sort(keyList);
    if (map.size() > 0)
      fireTableRowsInserted(0, map.size() - 1);
    refMap = properties;

  }

  @Override
  public Object getValueAt(int row, int column) {
    // PENDING JW: solve in getColumnClass instead of hacking here
    if (row >= getRowCount()) {
      return new Object();
    }

    switch (column) {
    case KEY_COLUMN:
      return keyList.get(row);
    case VALUE_COLUMN:
      return map.get(keyList.get(row));
    }
    return new Object();
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    switch (columnIndex) {
    case KEY_COLUMN:
      if (aValue == null) {
        map.remove(keyList.get(rowIndex));
        keyList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);

        // map.remove("");
        // keyList.remove(rowIndex);
        // fireTableRowsDeleted(rowIndex,rowIndex);
        return;
      }
      if (!keyList.get(rowIndex).equals(aValue)) {
        String value = map.get(keyList.get(rowIndex));

        map.remove(keyList.get(rowIndex));
        keyList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);

        if (map.get((String) aValue) != null)
          return;

        if (aValue.equals(""))
          return;

        int index = 0;
        for (; index < keyList.size(); index++) {
          if (keyList.get(index).compareTo((String) aValue) > 0)
            break;
        }

        keyList.add(index, (String) aValue);
        map.put((String) aValue, value);
        fireTableRowsInserted(index, index);
      }
      break;
    case VALUE_COLUMN:
      map.put(keyList.get(rowIndex), (String) aValue);
      break;
    }

  }

}
