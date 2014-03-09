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

import guitesting.ui.GUITester;
import guitesting.util.TestProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/*
 * This class is used for both TableModel and PropertyModel for event.
 * The information of component property will be stored in event.getComponentModel().map if eventModel is used, and use the method 'getRefMapPropertyKey(String)'if you want to retrieve info from TableModel in propertyPane  
 */
//@SuppressWarnings("serial")
public class PropertyModel extends AbstractTableModel implements Map<String, String>,Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String[] columnNames  = { "Key", "Value" };
    protected static final int     KEY_COLUMN   = 0;
    protected static final int     VALUE_COLUMN = 1;
    protected Map<String, String>  map          = new HashMap<String, String>();
    protected Map<String, String>  refMap;

    public PropertyModel()
    {
	super();
    }
    public PropertyModel(PropertyModel propertyModel) {
	super();
	this.map.putAll(propertyModel.map);
	this.refMap=propertyModel.refMap;
    }

    public void setproperty(Map<String, String> properties) {
	int size = size();
	map.clear();
	if (size > 0)
	    fireTableRowsDeleted(0, size - 1);
	map.putAll(properties);
	if(map.size()>0)
	    fireTableRowsInserted(0, map.size() - 1);
	refMap = properties;
    }
    
    public Map<String,String> getMap()
    {
	return map;    
    }
    
    public String getProperty(String key)
    {
	return map.get(key);
    }
    
    
    public String getRefMapProperty(String key)
    {
	return refMap.get(key);	
    }

    /**
     * save properties to the original properties which are actually used.
     */
    public void saveToRefMap() {
	if (refMap != null) {
	    refMap.clear();
	    refMap.putAll(map);
	    refMap.remove(""); //delete empty key(because of existing empty key at ValuePropertyModel)
	}
    }

    // --- implements Table Model ---

    @Override
    public String getColumnName(int column) {
	return columnNames[column];
    }

    @Override
    public int getRowCount() {
	return size();
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int column) {
	return getValueAt(0, column).getClass();
    }

    @Override
    public Object getValueAt(int row, int column) {
	// PENDING JW: solve in getColumnClass instead of hacking here
	if (row >= getRowCount()) {
	    return new Object();
	}

	int index = 0;
	// find a proper key for row
	String key = null;
	String value = null;
	for (String property : GUITester.getInstance().getGuiModelExtractor().getPropertyList()) {
	    value = get(property);
	    if (value != null) {
		if (row == index) {
		    key = property;
		    break;
		}
		index++;
	    }
	}
	if (key == null)
	    return new Object();

	switch (column) {
	case KEY_COLUMN:
	    return key;
	case VALUE_COLUMN:
	    return value;
	}
	return new Object();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex == 0)
	    return;

	int index = 0;
	// find a proper key for row
	String key = null;
	String value = null;
	for (String property : GUITester.getInstance().getGuiModelExtractor().getPropertyList()) {
	    value = get(property);
	    if (value != null) {
		if (rowIndex == index) {
		    key = property;
		    break;
		}
		index++;
	    }
	}
	if (key == null)
	    return;
	// delete if empty string is entered
	if (aValue.equals("")) {
	    map.remove(key);
	    fireTableRowsDeleted(rowIndex, rowIndex);
	} else
	    map.put(key, (String) aValue);
    }

    // --- implements Map<String,String> interface ---

    @Override
    public void clear() {
	map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
	return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return map.containsKey(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
	return map.entrySet();
    }

    @Override
    public String get(Object key) {
	return map.get(key);
    }

    @Override
    public boolean isEmpty() {
	return map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
	return map.keySet();
    }

    @Override
    public String put(String key, String value) {
	return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
	map.putAll(m);
    }

    @Override
    public String remove(Object key) {
	return map.remove(key);
    }

    @Override
    public int size() {
	return map.size();
    }

    @Override
    public Collection<String> values() {
	return map.values();
    }

}
