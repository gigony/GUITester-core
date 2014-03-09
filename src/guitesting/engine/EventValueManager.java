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
package guitesting.engine;

import guitesting.model.ComponentModel;
import guitesting.model.EventListModel;
import guitesting.model.event.EventModel;

import java.util.HashMap;
import java.util.Map;

public class EventValueManager {

  EventListModel customValueList = null;

  public EventValueManager(EventListModel customValueList) {
    this.customValueList = customValueList;
  }
  
  public void setCustomValueList(EventListModel customValueList) {
    this.customValueList = customValueList;
  }

  public EventListModel getCustomValueList() {
    return customValueList;
  }

  public Map<String, String> getMatchedCustomValues(EventModel event) {
    Map<String, String> result = new HashMap<String, String>();
    if (customValueList != null) {
      for (EventModel customValueItem : customValueList) {
        if (!customValueItem.getEventTypeName().equals(event.getEventTypeName())) // name should be matched
          continue;
        if (!isComponentMatched(customValueItem.getWindowModel(), event.getWindowModel())) // window model should be
                                                                                           // matched
          continue;
        if (!isComponentMatched(customValueItem.getComponentModel(), event.getComponentModel())) // component model
                                                                                                 // should be matched
          continue;
        // set value
        result.clear();
        result.putAll(customValueItem.getPropertyModel().getMap());
      }
    }
    return result;
  }

  private boolean isComponentMatched(ComponentModel src, ComponentModel target) {
    Map<String, String> srcProperties = src.getProperties();
    Map<String, String> targetProperties = target.getProperties();

    // target properties should contain all of src properties
    if (!targetProperties.keySet().containsAll(srcProperties.keySet()))
      return false;
    // each src property should equal with each target property
    for (String key : srcProperties.keySet()) {
      String value = srcProperties.get(key);
      if (value != null && !value.equals(targetProperties.get(key)))
        return false;
    }
    return true;
  }

}
