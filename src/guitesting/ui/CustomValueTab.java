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
package guitesting.ui;

import guitesting.model.ComponentModel;
import guitesting.model.EventListModel;
import guitesting.model.WindowModel;
import guitesting.model.event.EventModel;
import guitesting.util.ExtensionFileFilter;
import guitesting.util.JFCUtil;
import guitesting.util.TestProperty;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

@SuppressWarnings("serial")
public class CustomValueTab extends JXPanel {
  GUITesterFrame guiTesterFrame;
  final EventListModel customValues = new EventListModel();
  JXTable customValueTable;
  JButton loadButton;
  JButton saveButton;
  JButton applyButton;
  ColorHighlighter mouseClickHighlighter;

  public CustomValueTab(GUITesterFrame guiTesterFrame) {
    this.guiTesterFrame = guiTesterFrame;
    initComponents();
    configureComponents();
  }

  protected void initComponents() {
    setLayout(new BorderLayout());

    // add table
    customValueTable = JFCUtil.createXTable();
    customValueTable.setName("Filter");
    JScrollPane scrollPane = new JScrollPane(customValueTable);
    add(scrollPane);

    // add control buttons
    JPanel controlPanel = new JPanel();
    loadButton = new JButton("load");
    saveButton = new JButton("save");
    applyButton = new JButton("apply");
    controlPanel.add(loadButton);
    controlPanel.add(saveButton);
    controlPanel.add(applyButton);
    add(controlPanel, BorderLayout.SOUTH);
  }

  private void configureComponents() {

    mouseClickHighlighter = new ColorHighlighter(HighlightPredicate.NEVER, null, ColorUtil.setSaturation(Color.RED,
        0.9f));
    // update highlighter when table contents are changed by drag&drop
    customValues.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        updateHighlighter(customValueTable.getSelectedRow());
      }
    });

    customValueTable.setSortable(false);
    customValueTable.addHighlighter(mouseClickHighlighter);
    customValueTable.setColumnControlVisible(true);
    customValueTable.setShowGrid(false, false); // replace grid lines with striping
    customValueTable.addHighlighter(HighlighterFactory.createSimpleStriping());
    customValueTable.setVisibleRowCount(10); // initialize preferred size for table's viewable area
    CustomColumnFactory factory = new CustomColumnFactory(); // create and configure a custom column factory
    customValueTable.setColumnFactory(factory); // set the factory before setting the table model
    customValueTable.setModel(customValues);
    customValueTable.setDragEnabled(true);
    customValueTable.setDropMode(DropMode.INSERT_ROWS);
    customValueTable.setTransferHandler(new TransferHandler() {
      public boolean canImport(TransferHandler.TransferSupport support) {
        try {
          if (!support.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType))) {
            return false;
          }
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
          return false;
        }
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        if (dl.getRow() == -1) {
          return false;
        } else {
          return true;
        }
      }

      @Override
      public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
          return false;
        }
        Transferable transferable = support.getTransferable();
        EventModel data;
        try {
          Object transferedObj = transferable.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));
          if (!(transferedObj instanceof EventModel))
            return false;
          data = ((EventModel) transferedObj).crateCloneObj();
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }

        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        int index = dl.getRow();
        if (dl.isInsertRow()) {
          customValues.addEvent(index, data);
          customValueTable.packAll();

        }
        // scroll to display the element that was dropped
        customValueTable.scrollCellToVisible(index, 0);
        return true;
      }
    });
    customValueTable.setColumnSequence((Object[]) EventListModel.columnNames);
    customValueTable.setHorizontalScrollEnabled(true);
    customValueTable.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
          JPopupMenu popup = new JPopupMenu();
          JMenuItem menu = new JMenuItem("Delete");
          menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // remove events from the event list
              int removeIndexes[] = customValueTable.getSelectedRows();
              Arrays.sort(removeIndexes);
              for (int i = removeIndexes.length - 1; i >= 0; i--)
                customValues.removeEvent(removeIndexes[i]);
            }
          });
          popup.add(menu);
          popup.show(customValueTable, e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
          int row = customValueTable.getSelectedRow();
          if (row == -1)
            return;

          // view component info.
          ComponentModel component = customValues.getEvent(row).getComponentModel();
          WindowModel window = customValues.getEvent(row).getWindowModel();

          guiTesterFrame.propertyPane.componentPropertyTab.updateProperty(component.getProperties());
          guiTesterFrame.propertyPane.windowPropertyTab.updateProperty(window.getProperties());
          guiTesterFrame.propertyPane.valuePropertyTab.updateProperty(customValues.getEvent(row).getPropertyModel());

          guiTesterFrame.previewPane.viewComponent(component.getRef());

          updateHighlighter(row); // update highlighter
        }
      }
    });

    // add actions to buttons

    loadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open vustom values");
        fileChooser.setFileFilter(new ExtensionFileFilter("value(*.value)", new String[] { "value" }));
        if (fileChooser.showOpenDialog(CustomValueTab.this) == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          try {
            FileInputStream fin = new FileInputStream(file);
            EventListModel newTestCase = (EventListModel) TestProperty.xStream.fromXML(fin);
            customValues.setEventList(newTestCase);
            customValueTable.packAll();
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save custom values");
        fileChooser.setFileFilter(new ExtensionFileFilter("value(*.value)", new String[] { "value" }));
        if (fileChooser.showSaveDialog(CustomValueTab.this) == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          if (!file.getName().toLowerCase().endsWith(".value")) {
            file = new File(file.getAbsolutePath() + ".value");
          }
          try {
            FileOutputStream fout = new FileOutputStream(file);
            TestProperty.xStream.toXML(customValues, fout);
            fout.close();
            JOptionPane.showMessageDialog(CustomValueTab.this, "Save Succeed!");

          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e2) {
            e2.printStackTrace();
          }
        }
      }
    });

    applyButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TestProperty.eventValueManager.setCustomValueList(customValues);
        JOptionPane.showMessageDialog(CustomValueTab.this, "Apply Succeed!");
      }
    });

    // initialize custom value
    EventListModel initCustomValues = TestProperty.eventValueManager.getCustomValueList();
    if (initCustomValues != null) {
      customValues.setEventList(initCustomValues);
    }

  }

  /**
   * Highlight if same events are exist.
   * <p>
   * Property data is shared among same events. So, you should carefully modify properties.
   * </p>
   * 
   * @param index
   */
  public void updateHighlighter(int index) {
    mouseClickHighlighter.setHighlightPredicate(HighlightPredicate.NEVER);

    if (index == -1)
      return;

    final Set<Integer> sameSet = new HashSet<Integer>();
    EventModel event = customValues.getEvent(index);
    List<EventModel> eventSequence = customValues.getEventSequence();
    int count = eventSequence.size();
    for (int i = 0; i < count; i++) {
      if (i == index)
        continue;
      if (customValues.getEvent(i).equals(event)) {
        sameSet.add(i);
      }
    }

    HighlightPredicate predicate = new HighlightPredicate() {
      @Override
      public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
        return sameSet.contains(adapter.row);
      }
    };
    mouseClickHighlighter.setHighlightPredicate(predicate);
  }
}
