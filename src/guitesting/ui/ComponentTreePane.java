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

import guitesting.engine.modelextractor.GUIModelExtractor;
import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.model.PropertyModel;
import guitesting.model.WindowModel;
import guitesting.model.event.EventModel;
import guitesting.util.IOUtil;
import guitesting.util.JFCUtil;
import guitesting.util.LazyLoadingIconValue;
import guitesting.util.TestLogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * @author gigony
 * 
 */
@SuppressWarnings("serial")
public class ComponentTreePane extends JXTitledPanel implements MouseListener {
  GUITesterFrame guiTesterFrame;
  JXTreeTable treeTable = null;
  AbstractHighlighter mouseOverHighlighter;
  JButton refreshButton;
  JButton expandButton;
  JButton collapseButton;
  JToggleButton highlight;
  JToggleButton showOnlyEvent;
  JTextField commandField;
  JButton runCommandButton;

  public ComponentTreePane(GUITesterFrame guiTesterFrame) {
    this.guiTesterFrame = guiTesterFrame;
    initComponents();
    configureComponents();
  }

  private void initComponents() {
    setTitle("Component Tree");

    // add treeTable
    treeTable = new JXTreeTable();
    treeTable.setName("componentTreeTable");
    JScrollPane treeScrollPane = new JScrollPane(treeTable);
    treeScrollPane.setPreferredSize(new Dimension(300, 300));
    add(treeScrollPane);

    // add control buttons
    JPanel control = new JPanel();
    refreshButton = new JButton("Refresh");
    expandButton = new JButton("Expand tree");
    collapseButton = new JButton("Collapse tree");
    highlight = new JToggleButton("Highlight");
    showOnlyEvent = new JToggleButton("Show only events");
    commandField = new JTextField(8);
    runCommandButton = new JButton("Run CMD");
    control.add(refreshButton);
    control.add(expandButton);
    control.add(collapseButton);
    control.add(highlight);
    control.add(showOnlyEvent);
    control.add(commandField);
    control.add(runCommandButton);
    add(control, BorderLayout.SOUTH);
  }

  private void configureComponents() {

    // StringValue for tree node's text
    StringValue sv = new StringValue() {
      @Override
      public String getString(Object value) {
        if (value instanceof ComponentModel)
          return ((ComponentModel) value).get("title");
        if (value instanceof EventModel)
          return String.format("%s(%s[%s]):%s", ((EventModel) value).getComponentModel().get("title"),
              ((EventModel) value).getEventTypeName(), ((EventModel) value).getActionPropertyString(),
              ((EventModel) value).getHashCode());
        return StringValues.TO_STRING.getString(value);
      }
    };

    // StringValue for lazy icon loading
    StringValue keyValue = new StringValue() {
      @Override
      public String getString(Object value) {
        if (value instanceof ComponentModel) {
          return IOUtil.getSimpleClassName(((ComponentModel) value).get("class")) + ".png";
        }
        return StringValues.TO_STRING.getString(value);
      }
    };

    // IconValue provides node icon (these parts of code are from swingx demo)
    IconValue iv = new LazyLoadingIconValue(this.getClass(), keyValue, "fallback.png");
    // create and set a tree renderer using the custom Icon-/StringValue
    treeTable.setTreeCellRenderer(new DefaultTreeRenderer(iv, sv));

    // string representation for use of Dimension/Point class
    StringValue locSize = new StringValue() {

      @Override
      public String getString(Object value) {
        int x;
        int y;
        if (value instanceof Dimension) {
          x = ((Dimension) value).width;
          y = ((Dimension) value).height;
        } else if (value instanceof Point) {
          x = ((Point) value).x;
          y = ((Point) value).y;
        } else {
          return StringValues.TO_STRING.getString(value);
        }
        return "(" + x + ", " + y + ")";
      }
    };

    ColumnFactory factory = new ColumnFactory() {
      String[] columnNameKeys = { "Component Name", "Location", "Size" };

      @Override
      public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
        super.configureTableColumn(model, columnExt);
        if (columnExt.getModelIndex() < columnNameKeys.length) {
          columnExt.setTitle(columnNameKeys[columnExt.getModelIndex()]);
        }
      }
    };
    // set treeTable

    mouseOverHighlighter = new ColorHighlighter(HighlightPredicate.NEVER, ColorUtil.setSaturation(Color.MAGENTA, 0.3f),
        null);
    treeTable.addHighlighter(mouseOverHighlighter);
    treeTable.setDefaultRenderer(Point.class, new DefaultTableRenderer(locSize, JLabel.CENTER));
    treeTable.setDefaultRenderer(Dimension.class, treeTable.getDefaultRenderer(Point.class));
    treeTable.setColumnFactory(factory);
    treeTable.setHorizontalScrollEnabled(true);
    treeTable.setColumnControlVisible(true);
    treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    treeTable.setDragEnabled(true);
    treeTable.addMouseListener(this);
    treeTable.setTransferHandler(new TransferHandler() {
      @Override
      public int getSourceActions(JComponent c) {
        return COPY;
      }

      @Override
      protected Transferable createTransferable(JComponent component) {
        int row = treeTable.getSelectedRow();
        if (row == -1)
          return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeTable.getPathForRow(row).getLastPathComponent();
        if (node != null) {
          Object userObj = node.getUserObject();
          if (userObj instanceof EventModel)
            return (EventModel) userObj;
        }
        return null;
      }
    });
    // set initial model not to raise exception(tooltip related in JXTreeTable..)
    treeTable.setTreeTableModel(new DefaultTreeTableModel(new GUIModel(true)));

    // add listener to buttons

    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIModel model = guiTesterFrame.guiTester.getGuiModelExtractor().getGUIModel();

        guiTesterFrame.updateGUIModel(model);

      }
    });
    expandButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        treeTable.expandAll();
      }
    });
    collapseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        treeTable.collapseAll();
      }
    });
    highlight.setSelected(true);
    showOnlyEvent.setSelected(true);
    runCommandButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final UIAction action = guiTesterFrame.guiTester.getUIAction();
        if (action != null) {
          try {
            new Thread(new Runnable() {
              @Override
              public void run() {
                action.runCommand(commandField.getText());
              }
            }).start();

          } catch (Throwable t) {
            TestLogger.log.error("afterPerformEventUI execution failed!", t);
          }
        }

      }
    });

  }

  public void updateHighlighter(Object component) {
    if (!highlight.isSelected())
      return;
    mouseOverHighlighter.setHighlightPredicate(HighlightPredicate.NEVER);
    if (component != null) {
      TreeTableModel treeModel = (TreeTableModel) treeTable.getTreeTableModel();

      GUIModel model = (GUIModel) treeModel.getRoot();
      if (model == null)
        return;

      final Set<Integer> rowSet = new HashSet<Integer>(); // storage for nodes which will be highlighted

      boolean scrolled = false;
      Enumeration<?> enumurator = model.breadthFirstEnumeration();
      while (enumurator.hasMoreElements()) {
        GUIModel guiModel = (GUIModel) enumurator.nextElement();
        Object userObj = guiModel.getUserObject();

        ComponentModel componentModel = null;
        if (userObj instanceof ComponentModel)
          componentModel = (ComponentModel) userObj;
        else if (userObj instanceof EventModel)
          componentModel = ((EventModel) userObj).getComponentModel();

        if (componentModel == null || componentModel.getRef() == null) // if a root node or a reference is disappeared
          continue;

        if (componentModel.getRef() == component) {
          final TreePath treePath = new TreePath(guiModel.getPath());
          if (!scrolled) {
            treeTable.scrollPathToVisible(treePath); // scroll to which the matched component exist
            scrolled = true;
          }
          rowSet.add(treeTable.getRowForPath(treePath));
        }
      }

      HighlightPredicate predicate = new HighlightPredicate() {

        @Override
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
          return rowSet.contains(adapter.row);
        }
      };
      mouseOverHighlighter.setHighlightPredicate(predicate);
    }
  }

  /**
   * update component tree using <tt>model</tt> object.
   * 
   * @param model
   */
  public void updateGUIModel(final GUIModel model) {
    // !!! be careful not to modify original model in this implementation. !!!
    try {
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          GUIModel duplicatedModel = JFCUtil.shallowTreeCopy(model); // copy GUI model to be used to display tree
          if (showOnlyEvent.isSelected())
            duplicatedModel = createEventGUIModel(duplicatedModel);
          else
            GUIModelExtractor.addEventNode(duplicatedModel);
          TreeTableModel treeModel = new DefaultTreeTableModel(duplicatedModel);
          treeTable.setTreeTableModel(treeModel);
          expandOnlyEvents(treeModel); // expand only events
          treeTable.packColumn(treeTable.getHierarchicalColumn(), -1);

        }

      });
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Create model which event nodes are added. The internal state of input object will be modified.
   * 
   * @param model
   * @return
   */
  @SuppressWarnings("unchecked")
  private GUIModel createEventGUIModel(GUIModel model) {
    GUIModel newModel = new GUIModel(true);
    GUIModel root = (GUIModel) model.getRoot();

    for (Enumeration<GUIModel> enumuration = (Enumeration<GUIModel>) root.depthFirstEnumeration(); enumuration
        .hasMoreElements();) {
      GUIModel node = enumuration.nextElement();
      Object userObj = node.getUserObject();
      if (userObj instanceof ComponentModel) {
        ComponentModel component = (ComponentModel) userObj;
        List<EventModel> eventList = component.getEventList();
        if (eventList.size() > 0 && "true".equals(component.get("visible")) && "true".equals(component.get("enabled"))) {
          for (EventModel event : eventList) {
            GUIModel childNode = new GUIModel(event);
            node.add(childNode);
            if (event.isContainer()) { // add child nodes if an event is container
              List<EventModel> childrenEventList = event.getChildren();
              for (EventModel childEvent : childrenEventList) {
                GUIModel childEventNode = new GUIModel(childEvent);
                childNode.add(childEventNode);
              }
            }
            // add only childNode to newModel
            newModel.add(childNode); // the position of this statement is important!
          }

        }
      }
    }
    return newModel;
  }

  private void expandOnlyEvents(TreeTableModel model) {
    GUIModel root = (GUIModel) model.getRoot();
    GUIModel leaf = (GUIModel) root.getFirstLeaf();

    while (leaf != null) {
      Object userObj = leaf.getUserObject();
      if (userObj instanceof EventModel) {
        treeTable.expandPath(new TreePath(((GUIModel) leaf.getParent()).getPath())); // a path should not be leaf node
                                                                                     // when expand path
      }
      leaf = (GUIModel) leaf.getNextLeaf();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 0) {
      if (treeTable.getSelectedRow() == -1)
        return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeTable.getPathForRow(treeTable.getSelectedRow())
          .getLastPathComponent();
      if (node != null) {
        Object userObj = node.getUserObject();
        if (userObj instanceof EventModel) {
          final EventModel event = (EventModel) userObj;
          switch (e.getClickCount()) {
          case 1:
            // view component info.
            ComponentModel component = event.getComponentModel();
            WindowModel window = event.getWindowModel();
            guiTesterFrame.propertyPane.componentPropertyTab.updateProperty(component.getProperties());
            if (window != null)
              guiTesterFrame.propertyPane.windowPropertyTab.updateProperty(window.getProperties());
            guiTesterFrame.propertyPane.valuePropertyTab.updateProperty(event.getPropertyModel());

            guiTesterFrame.previewPane.viewComponent(component.getRef());
            break;

          case 2:
            // do action related to UI from the outside of UI
            final UIAction action = GUITester.getInstance().getUIAction();

            if (action != null) {
              action.beforePerformEventUI(event);
            }

            // perform event
            event.perform();

            // delay
            GUITester.getInstance().getDelayManager().delayEventIntervalTime();

            // add to test case
            guiTesterFrame.editorPane.testCaseTab.testcase.addEvent(event);

            if (action != null) {
              try {
                new Thread(new Runnable() {
                  @Override
                  public void run() {
                    action.afterPerformEventUI(event);
                  }
                }).start();

              } catch (Throwable t) {
                TestLogger.log.error("afterPerformEventUI execution failed!", t);
              }
            }

            // refresh
            GUIModel model = GUITester.getInstance().getGuiModelExtractor().getGUIModel();
            guiTesterFrame.updateGUIModel(model);
          }
        } else if (userObj instanceof ComponentModel) {
          if (e.getClickCount() == 1) {
            // view component info.
            ComponentModel component = (ComponentModel) userObj;
            guiTesterFrame.propertyPane.componentPropertyTab.updateProperty(component.getProperties());
            guiTesterFrame.propertyPane.windowPropertyTab.updateProperty(new PropertyModel());
            guiTesterFrame.propertyPane.valuePropertyTab.updateProperty(new PropertyModel());

            guiTesterFrame.previewPane.viewComponent(component.getRef());
          }
        }

      }

    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }
}
