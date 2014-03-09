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
import guitesting.model.event.EventModel;
import guitesting.util.TestLogger;
import guitesting.util.TestProperty;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
  private static EventManager instance;

  public static EventManager getInstance() {
    if (instance == null) {
      synchronized (EventManager.class) {
        if (instance == null) {
          instance = new EventManager();
        }
      }
    }
    return instance;
  }

  private EventManager() {
  }

  Map<String, Class<? extends EventModel>> eventHandlerMap = new HashMap<String, Class<? extends EventModel>>();
  Map<Class<? extends EventModel>, EventModel> eventHandlerObjPool = new HashMap<Class<? extends EventModel>, EventModel>();
  List<Class<? extends EventModel>> eventHandlerObjList = new ArrayList<Class<? extends EventModel>>();

  public void init() {
    eventHandlerMap.clear(); // clear eventHandlerMap
    eventHandlerObjPool.clear(); // clear eventHandlerObjPool
    eventHandlerObjList.clear(); // clear eventHandlerObjList
  }

  public void registerEventHandler(Class<? extends EventModel> cl) {
    try {
      Constructor<? extends EventModel> constructor = cl.getConstructor(new Class<?>[] { ComponentModel.class });
      EventModel event = (EventModel) constructor.newInstance(new Object[] { null });
      String eventName = event.getEventTypeName();
      if (eventName == null)
        throw new Exception("Event name can not be found!");
      if (eventHandlerObjPool.containsKey(cl))
        TestLogger.log.warn(String.format("\tThe same event exists (%s) ...Fail", cl.getSimpleName()));
      else {
        eventHandlerMap.put(eventName, cl);
        eventHandlerObjPool.put(cl, event);
        eventHandlerObjList.add(cl);
        TestLogger.debug("\tEvent handler plugin... %s (%s) is set.", cl.getName(), eventName);

      }
    } catch (Throwable t) {
      t.printStackTrace();
      TestLogger.warn("\tEvent handler plugin... %s - Failed (%s)", cl.getName(), t);

    }
  }

  public void registerEventHandlerLists(String eventHandlers) {
    String[] handlerNameList = eventHandlers.split("[\\:\\;]");
    if (handlerNameList.length == 0) {
      TestLogger.error("There is no event handler list!");
      return;
    }

    for (String eventHandlerName : handlerNameList) {
      try {
        Class<? extends EventModel> handlerClass = null;
        if (TestProperty.pluginClassLoader.containsClass(eventHandlerName)) {
          handlerClass = (Class<? extends EventModel>) Class.forName(eventHandlerName, true,
              TestProperty.pluginClassLoader);
        } else {
          try {
            handlerClass = (Class<? extends EventModel>) Class.forName("guitesting.model.event." + eventHandlerName
                + "_" + TestProperty.propertyStore.get("guitester.platform"));
          } catch (Throwable t) {
            handlerClass = (Class<? extends EventModel>) Class.forName("guitesting.model.event." + eventHandlerName);
          }
        }

        registerEventHandler(handlerClass);
      } catch (Throwable t) {
        t.printStackTrace();
        TestLogger.log.warn(String.format("\tevent %s ... Fail(%s)", eventHandlerName, t));
      }
    }
  }

  public Collection<Class<? extends EventModel>> getEventHandlerClassesSet() {
    return eventHandlerMap.values();
  }

  public Collection<Class<? extends EventModel>> getEventHandlerClassesList() {
    return eventHandlerObjList;
  }

  public Collection<String> getEventHandlerNames() {
    return eventHandlerMap.keySet();
  }

  public Class<? extends EventModel> getEventHandlerByName(String name) {
    return eventHandlerMap.get(name);
  }

  public EventModel getEventFromPool(Class<? extends EventModel> classObj) {
    return eventHandlerObjPool.get(classObj);
  }

  public EventModel getEventHandlerObject(String name) {
    return eventHandlerObjPool.get(getEventHandlerByName(name));
  }

}
