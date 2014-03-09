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

import guitesting.engine.EventManager;
import guitesting.model.event.EventModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.table.AbstractTableModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("serial")
@XStreamAlias("eventlist")
public class EventListModel extends AbstractTableModel implements Converter, List<EventModel> {

  public static final int NO_COLUMN = 0;
  public static final int EVENT_COLUMN = 1;
  public static final int COMPONENT_COLUMN = 2;
  public static final int WINDOW_COLUMN = 3;

  public static final String[] columnNames = { "No.", "Event", "Component", "Window" };

  private List<EventModel> eventSequence = null;

  public EventListModel() {
    super();
    eventSequence = new ArrayList<EventModel>();
  }

  public EventListModel(List<EventModel> eventSequence) {
    super();
    this.eventSequence = new ArrayList<EventModel>(eventSequence);
  }

  public void setEventSequence(List<EventModel> eventSequence) {
    this.eventSequence = eventSequence;
  }

  public List<EventModel> getEventSequence() {
    return eventSequence;
  }

  // --- implements Converter interface ---

  public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
    EventListModel eventList = (EventListModel) source;

    List<EventModel> eventSeq = eventList.getEventSequence();
    for (EventModel event : eventSeq) {
      writer.startNode("event");
      context.convertAnother(event);
      writer.endNode();
    }

  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    EventListModel eventList = new EventListModel();

    while (reader.hasMoreChildren()) {
      reader.moveDown();
      String eventName = reader.getAttribute("name");
      EventModel event = (EventModel) context.convertAnother(null,
          EventManager.getInstance().getEventHandlerByName(eventName));
      eventList.addEvent(event);
      reader.moveUp();
    }

    return eventList;
  }

  public boolean canConvert(Class type) {
    return EventListModel.class.equals(type);
  }

  // --- override AbstractTableModel ---

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  public int getRowCount() {
    return eventSequence.size();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Class<?> getColumnClass(int column) {
    Object result = getValueAt(0, column);
    if (result == null)
      result = "nu";
    return result.getClass();
  }

  public Object getValueAt(int row, int column) {
    // PENDING JW: solve in getColumnClass instead of hacking here
    if (row >= getRowCount()) {
      return new Object();
    }
    switch (column) {
    case NO_COLUMN:
      return row;
    case EVENT_COLUMN:
      return String.format("%s[%s]", getEvent(row).getEventTypeName(), getEvent(row).getActionPropertyString());
    case COMPONENT_COLUMN:
      return getEvent(row).getComponentModel().get("title");
    case WINDOW_COLUMN:
      if (getEvent(row).getWindowModel() != null)
        return getEvent(row).getWindowModel().get("title");
      return null;
    }
    return null;
  }

  public void addEvents(List<EventModel> newEvents) {
    if(newEvents.isEmpty())
      return;
    int first = eventSequence.size();
    int last = first + newEvents.size() - 1;
    eventSequence.addAll(newEvents);
    fireTableRowsInserted(first, last);
  }

  public void addEvent(EventModel event) {
    int index = eventSequence.size();
    eventSequence.add(event);
    fireTableRowsInserted(index, index);
  }

  public void addEvent(int index, EventModel event) {
    eventSequence.add(index, event);
    fireTableRowsInserted(index, index);
  }

  public void removeEvent(int index) {
    eventSequence.remove(index);
    fireTableRowsDeleted(index, index);
  }

  public void setEventList(EventListModel newTestCase) {
    int size = eventSequence.size();
    eventSequence.clear();
    if (size > 0)
      fireTableRowsDeleted(0, size - 1);
    addEvents(newTestCase.getEventSequence());
  }

  public int getEventCount() {
    return eventSequence.size();
  }

  public EventModel getEvent(int index) {
    return eventSequence.get(index);
  }

  // --- override List<EventModel> ---

  public int size() {
    return eventSequence.size();
  }

  public boolean isEmpty() {
    return eventSequence.isEmpty();
  }

  public boolean contains(Object o) {
    return eventSequence.contains(o);
  }

  public Iterator<EventModel> iterator() {
    return eventSequence.iterator();
  }

  public Object[] toArray() {
    return eventSequence.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return eventSequence.toArray(a);
  }

  public boolean add(EventModel e) {
    return eventSequence.add(e);
  }

  public boolean remove(Object o) {
    return eventSequence.remove(o);
  }

  public boolean containsAll(Collection<?> c) {
    return eventSequence.containsAll(c);
  }

  public boolean addAll(Collection<? extends EventModel> c) {
    return eventSequence.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends EventModel> c) {
    return eventSequence.addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return eventSequence.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return eventSequence.retainAll(c);
  }

  public void clear() {
    eventSequence.clear();
  }

  public EventModel get(int index) {
    return eventSequence.get(index);
  }

  public EventModel set(int index, EventModel element) {
    return eventSequence.set(index, element);
  }

  public void add(int index, EventModel element) {
    eventSequence.add(index, element);
  }

  public EventModel remove(int index) {
    return eventSequence.remove(index);
  }

  public int indexOf(Object o) {
    return eventSequence.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return eventSequence.lastIndexOf(o);
  }

  public ListIterator<EventModel> listIterator() {
    return eventSequence.listIterator();
  }

  public ListIterator<EventModel> listIterator(int index) {
    return eventSequence.listIterator(index);
  }

  public List<EventModel> subList(int fromIndex, int toIndex) {
    return eventSequence.subList(fromIndex, toIndex);
  }

}
