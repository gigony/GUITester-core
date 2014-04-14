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

import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.model.ComponentModel;
import guitesting.model.EventListModel;
import guitesting.model.WindowModel;
import guitesting.model.event.EventModel;

import java.util.ArrayList;
import java.util.Map;

public class EventFilter {

  EventListModel filterList = null;

  public void setFilter(EventListModel filterList) {
    this.filterList = filterList;

  }

  public EventListModel getFilter() {
    return filterList;
  }

  public boolean shouldFilterComponent(ComponentModel componentModel) {
    if (filterList != null) {
      WindowModel windowModel = GUIModelExtractor.getWindowModel(componentModel);

      if (windowModel == null)
        return false;

      for (EventModel filterItem : filterList) {
        if (filterItem == null)
          continue;
        if (!isComponentMatched(filterItem.getWindowModel(), windowModel)) // window model should be matched
          continue;
        if (!isComponentMatched(filterItem.getComponentModel(), componentModel)) // component model should be matched
          continue;
        return true;
      }
    }
    return false;
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
      if (value != null) {
        if (value.startsWith("@\"") && value.endsWith("\"") && value.length() > 3) {
          // if regular expression - deprecated
          value = value.substring(2, value.length() - 1);
          if (!targetProperties.get(key).matches(value))
            return false;
        } else if (value.startsWith("r@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if regular expression r@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).matches(value))
            return false;
        } else if (value.startsWith("e@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if ending string e@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).endsWith(value))
            return false;
        } else if (value.startsWith("s@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if starting string s@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).startsWith(value))
            return false;
        } else if (value.startsWith("c@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if substring c@"[word]"
          value = value.substring(3, value.length() - 1);
          if (targetProperties.get(key).indexOf(value) < 0)
            return false;
        } else if (!value.equalsIgnoreCase(targetProperties.get(key)))
          return false;
      }
    }
    return true;
  }

  public void replaceComponentString(ComponentModel componentModel) {

    if (filterList != null) {
      for (EventModel filterItem : filterList) {
        // component model should be matched
        if (!isComponentReplaceMatched(filterItem.getComponentModel(), componentModel))
          continue;

        replaceComponentString(filterItem.getComponentModel(), componentModel);

        return;
      }
    }
    return;
  }

  private void replaceComponentString(ComponentModel src, ComponentModel target) {
    Map<String, String> srcProperties = src.getProperties();
    Map<String, String> targetProperties = target.getProperties();

    // target properties should contain all of src properties
    if (!targetProperties.keySet().containsAll(srcProperties.keySet()))
      return;

    // each src property should equal with each target property
    for (String key : new ArrayList<String>(srcProperties.keySet())) {
      String targetString = srcProperties.get(key);
      if (targetString != null) {

        if (targetString.startsWith("r@\"") && targetString.endsWith("\"") && targetString.indexOf("/") > 3) {
          // if regular expression r@"[word]"/"[replacement]"
          String replacement = targetString.substring(targetString.indexOf("\"/\"") + 3, targetString.length() - 1);
          targetString = targetString.substring(3, targetString.indexOf("\"/\""));

          if (targetProperties.get(key).matches(targetString)) {
            targetProperties.put(key, replacement); // replace string
          }
        } else if (targetString.startsWith("e@\"") && targetString.endsWith("\"") && targetString.indexOf("/") > 3) {
          // if ends with e@"[end word]"/"[replacement]"
          String replacement = targetString.substring(targetString.indexOf("\"/\"") + 3, targetString.length() - 1);
          targetString = targetString.substring(3, targetString.indexOf("\"/\""));

          if (targetProperties.get(key).endsWith(targetString)) {
            targetProperties.put(key, replacement); // replace string
          }
        } else if (targetString.startsWith("s@\"") && targetString.endsWith("\"") && targetString.indexOf("\"/\"") > 3) {
          // if starts with s@"[start word]"/"[replacement]"
          String replacement = targetString.substring(targetString.indexOf("\"/\"") + 3, targetString.length() - 1);
          targetString = targetString.substring(3, targetString.indexOf("\"/\""));

          if (targetProperties.get(key).startsWith(targetString)) {
            targetProperties.put(key, replacement); // replace string
          }
        } else if (targetString.startsWith("c@\"") && targetString.endsWith("\"") && targetString.indexOf("\"/\"") > 3) {
          // if substring c@"[sub-word]"/"[replacement]"
          String replacement = targetString.substring(targetString.indexOf("\"/\"") + 3, targetString.length() - 1);
          targetString = targetString.substring(3, targetString.indexOf("\"/\""));

          if (targetProperties.get(key).indexOf(targetString) >= 0) {
            targetProperties.put(key, replacement); // replace string
          }
        }
      }
    }

  }

  private boolean isComponentReplaceMatched(ComponentModel src, ComponentModel target) {
    Map<String, String> srcProperties = src.getProperties();
    Map<String, String> targetProperties = target.getProperties();

    // target properties should contain all of src properties
    if (!targetProperties.keySet().containsAll(srcProperties.keySet()))
      return false;
    // each src property should equal with each target property
    for (String key : srcProperties.keySet()) {
      String value = srcProperties.get(key);
      if (value != null) {
        if (value.startsWith("@\"") && value.endsWith("\"") && value.length() > 3) // if regular expression -deprecated
        {
          value = value.substring(2, value.length() - 1);
          if (!targetProperties.get(key).matches(value))
            return false;
        } else if (value.startsWith("r@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if regular expression r@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).matches(value))
            return false;
        } else if (value.startsWith("r@\"") && value.endsWith("\"") && value.indexOf("\"/\"") > 3) {
          // if regular expression r@"[word]"/"[replacement]"
          value = value.substring(3, value.indexOf("\"/\""));
          if (!targetProperties.get(key).matches(value))
            return false;
        } else if (value.startsWith("e@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if ending string e@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).endsWith(value))
            return false;
        } else if (value.startsWith("e@\"") && value.endsWith("\"") && value.indexOf("\"/\"") > 3) {
          // if ending string e@"[word]"/"[replacement]"
          value = value.substring(3, value.indexOf("\"/\""));
          if (!targetProperties.get(key).endsWith(value))
            return false;
        } else if (value.startsWith("s@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if starting string s@"[word]"
          value = value.substring(3, value.length() - 1);
          if (!targetProperties.get(key).startsWith(value))
            return false;
        } else if (value.startsWith("s@\"") && value.endsWith("\"") && value.indexOf("\"/\"") > 3) {
          // if starting string s@"[word]"/"[replacement]"
          value = value.substring(3, value.indexOf("\"/\""));
          if (!targetProperties.get(key).startsWith(value))
            return false;
        } else if (value.startsWith("c@\"") && value.endsWith("\"") && value.indexOf("\"/\"") < 0) {
          // if substring c@"[word]"
          value = value.substring(3, value.length() - 1);
          if (targetProperties.get(key).indexOf(value) < 0)
            return false;
        } else if (value.startsWith("c@\"") && value.endsWith("\"") && value.indexOf("\"/\"") > 3) {
          // if substring c@"[word]"/"[replacement]"
          value = value.substring(3, value.indexOf("\"/\""));
          if (targetProperties.get(key).indexOf(value) < 0)
            return false;
        } else if (!value.equalsIgnoreCase(targetProperties.get(key)))
          return false;
      }
    }
    return true;
  }

}
