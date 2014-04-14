/*******************************************************************************
 * All rights reserved.
 * Copyright (c) 2010-2011, Gigon Bae
 *  
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  
 *     1. Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *     
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *     3. Neither the name of this project nor the names of its contributors may be
 *        used to endorse or promote products derived from this software without
 *        specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package guitesting.util;

import guitesting.model.ComponentModel;
import guitesting.model.GUIModel;
import guitesting.model.WindowModel;
import guitesting.model.event.EventModel;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorListener;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

public class JFCUtil {
  private static List<String> actionListenerNameList = Arrays.asList("getActionListeners", "getAncestorListeners",
      "getWindowListeners", "getComponentListeners", "getKeyListeners", "getMouseListeners", "getMouseMotionListeners",
      "getMouseWheelListeners", "getPropertyChangeListeners", "getVetoableChangeListeners", "getContainerListeners",
      "getFocusListeners", "getHierarchyBoundsListeners", "getHierarchyBoundsListeners", "getInputMethodListeners");

  public final static String[] listenerClassNameList = { "java.awt.AWTEventMulticaster",
      "java.awt.Checkbox$AccessibleAWTCheckbox",
      "java.awt.Component$AccessibleAWTComponent$AccessibleAWTComponentHandler",
      "java.awt.Component$AccessibleAWTComponent$AccessibleAWTFocusHandler",
      "java.awt.Container$AccessibleAWTContainer$AccessibleContainerHandler", "java.awt.List$AccessibleAWTList",
      "java.awt.TextArea$AccessibleAWTTextArea", "java.awt.TextComponent$AccessibleAWTTextComponent",
      "java.awt.TextField$AccessibleAWTTextField", "java.awt.datatransfer.FlavorListener",
      "java.awt.dnd.DragGestureListener", "java.awt.dnd.DragSourceAdapter", "java.awt.dnd.DragSourceContext",
      "java.awt.dnd.DragSourceListener", "java.awt.dnd.DragSourceMotionListener", "java.awt.dnd.DropTarget",
      "java.awt.dnd.DropTarget$DropTargetAutoScroller", "java.awt.dnd.DropTargetAdapter",
      "java.awt.dnd.DropTargetListener", "java.awt.dnd.MouseDragGestureRecognizer", "java.awt.event.AWTEventListener",
      "java.awt.event.AWTEventListenerProxy", "java.awt.event.ActionListener", "java.awt.event.AdjustmentListener",
      "java.awt.event.ComponentAdapter", "java.awt.event.ComponentListener", "java.awt.event.ContainerAdapter",
      "java.awt.event.ContainerListener", "java.awt.event.FocusAdapter", "java.awt.event.FocusListener",
      "java.awt.event.HierarchyBoundsAdapter", "java.awt.event.HierarchyBoundsListener",
      "java.awt.event.HierarchyListener", "java.awt.event.InputMethodListener", "java.awt.event.ItemListener",
      "java.awt.event.KeyAdapter", "java.awt.event.KeyListener", "java.awt.event.MouseAdapter",
      "java.awt.event.MouseListener", "java.awt.event.MouseMotionAdapter", "java.awt.event.MouseMotionListener",
      "java.awt.event.MouseWheelListener", "java.awt.event.TextListener", "java.awt.event.WindowAdapter",
      "java.awt.event.WindowFocusListener", "java.awt.event.WindowListener", "java.awt.event.WindowStateListener",
      "javax.swing.event.AncestorListener", "javax.swing.event.CaretListener", "javax.swing.event.CellEditorListener",
      "javax.swing.event.ChangeListener", "javax.swing.event.DocumentListener", "javax.swing.event.HyperlinkListener",
      "javax.swing.event.InternalFrameAdapter", "javax.swing.event.InternalFrameListener",
      "javax.swing.event.ListDataListener", "javax.swing.event.ListSelectionListener",
      "javax.swing.event.MenuDragMouseListener", "javax.swing.event.MenuKeyListener", "javax.swing.event.MenuListener",
      "javax.swing.event.MouseInputAdapter", "javax.swing.event.MouseInputListener",
      "javax.swing.event.PopupMenuListener", "javax.swing.event.RowSorterListener",
      "javax.swing.event.TableColumnModelListener", "javax.swing.event.TableModelListener",
      "javax.swing.event.TreeExpansionListener", "javax.swing.event.TreeModelListener",
      "javax.swing.event.TreeSelectionListener", "javax.swing.event.TreeWillExpandListener",
      "javax.swing.event.UndoableEditListener", "javax.swing.plaf.basic.BasicButtonListener",
      "javax.swing.plaf.basic.BasicColorChooserUI$PropertyHandler", "javax.swing.plaf.basic.BasicComboBoxEditor",
      "javax.swing.plaf.basic.BasicComboBoxEditor$UIResource", "javax.swing.plaf.basic.BasicComboBoxUI$FocusHandler",
      "javax.swing.plaf.basic.BasicComboBoxUI$ItemHandler", "javax.swing.plaf.basic.BasicComboBoxUI$KeyHandler",
      "javax.swing.plaf.basic.BasicComboBoxUI$ListDataHandler",
      "javax.swing.plaf.basic.BasicComboBoxUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicComboPopup$InvocationKeyHandler",
      "javax.swing.plaf.basic.BasicComboPopup$InvocationMouseHandler",
      "javax.swing.plaf.basic.BasicComboPopup$InvocationMouseMotionHandler",
      "javax.swing.plaf.basic.BasicComboPopup$ItemHandler", "javax.swing.plaf.basic.BasicComboPopup$ListDataHandler",
      "javax.swing.plaf.basic.BasicComboPopup$ListMouseHandler",
      "javax.swing.plaf.basic.BasicComboPopup$ListMouseMotionHandler",
      "javax.swing.plaf.basic.BasicComboPopup$ListSelectionHandler",
      "javax.swing.plaf.basic.BasicComboPopup$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicDesktopIconUI$MouseInputHandler",
      "javax.swing.plaf.basic.BasicDesktopPaneUI$CloseAction",
      "javax.swing.plaf.basic.BasicDesktopPaneUI$MaximizeAction",
      "javax.swing.plaf.basic.BasicDesktopPaneUI$MinimizeAction",
      "javax.swing.plaf.basic.BasicDesktopPaneUI$NavigateAction",
      "javax.swing.plaf.basic.BasicDesktopPaneUI$OpenAction", "javax.swing.plaf.basic.BasicDirectoryModel",
      "javax.swing.plaf.basic.BasicFileChooserUI$ApproveSelectionAction",
      "javax.swing.plaf.basic.BasicFileChooserUI$CancelSelectionAction",
      "javax.swing.plaf.basic.BasicFileChooserUI$ChangeToParentDirectoryAction",
      "javax.swing.plaf.basic.BasicFileChooserUI$DoubleClickListener",
      "javax.swing.plaf.basic.BasicFileChooserUI$GoHomeAction",
      "javax.swing.plaf.basic.BasicFileChooserUI$NewFolderAction",
      "javax.swing.plaf.basic.BasicFileChooserUI$SelectionListener",
      "javax.swing.plaf.basic.BasicFileChooserUI$UpdateAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$CloseAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$IconifyAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$MaximizeAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$MoveAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$RestoreAction",
      "javax.swing.plaf.basic.BasicInternalFrameTitlePane$SizeAction",
      "javax.swing.plaf.basic.BasicInternalFrameUI$BasicInternalFrameListener",
      "javax.swing.plaf.basic.BasicInternalFrameUI$BorderListener",
      "javax.swing.plaf.basic.BasicInternalFrameUI$ComponentHandler",
      "javax.swing.plaf.basic.BasicInternalFrameUI$GlassPaneDispatcher",
      "javax.swing.plaf.basic.BasicInternalFrameUI$InternalFramePropertyChangeListener",
      "javax.swing.plaf.basic.BasicLabelUI", "javax.swing.plaf.basic.BasicListUI$FocusHandler",
      "javax.swing.plaf.basic.BasicListUI$ListDataHandler", "javax.swing.plaf.basic.BasicListUI$ListSelectionHandler",
      "javax.swing.plaf.basic.BasicListUI$MouseInputHandler",
      "javax.swing.plaf.basic.BasicListUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicMenuItemUI$MouseInputHandler", "javax.swing.plaf.basic.BasicMenuUI$ChangeHandler",
      "javax.swing.plaf.basic.BasicMenuUI$MouseInputHandler",
      "javax.swing.plaf.basic.BasicOptionPaneUI$ButtonActionListener",
      "javax.swing.plaf.basic.BasicOptionPaneUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicProgressBarUI$ChangeHandler", "javax.swing.plaf.basic.BasicRootPaneUI",
      "javax.swing.plaf.basic.BasicScrollBarUI$ArrowButtonListener",
      "javax.swing.plaf.basic.BasicScrollBarUI$ModelListener",
      "javax.swing.plaf.basic.BasicScrollBarUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicScrollBarUI$ScrollListener",
      "javax.swing.plaf.basic.BasicScrollBarUI$TrackListener",
      "javax.swing.plaf.basic.BasicScrollPaneUI$HSBChangeListener",
      "javax.swing.plaf.basic.BasicScrollPaneUI$MouseWheelHandler",
      "javax.swing.plaf.basic.BasicScrollPaneUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicScrollPaneUI$VSBChangeListener",
      "javax.swing.plaf.basic.BasicScrollPaneUI$ViewportChangeHandler",
      "javax.swing.plaf.basic.BasicSliderUI$ActionScroller", "javax.swing.plaf.basic.BasicSliderUI$ChangeHandler",
      "javax.swing.plaf.basic.BasicSliderUI$ComponentHandler", "javax.swing.plaf.basic.BasicSliderUI$FocusHandler",
      "javax.swing.plaf.basic.BasicSliderUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicSliderUI$ScrollListener", "javax.swing.plaf.basic.BasicSliderUI$TrackListener",
      "javax.swing.plaf.basic.BasicSplitPaneDivider", "javax.swing.plaf.basic.BasicSplitPaneDivider$MouseHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$FocusHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$KeyboardDownRightHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$KeyboardEndHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$KeyboardHomeHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$KeyboardResizeToggleHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$KeyboardUpLeftHandler",
      "javax.swing.plaf.basic.BasicSplitPaneUI$PropertyHandler",
      "javax.swing.plaf.basic.BasicTabbedPaneUI$FocusHandler", "javax.swing.plaf.basic.BasicTabbedPaneUI$MouseHandler",
      "javax.swing.plaf.basic.BasicTabbedPaneUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicTabbedPaneUI$TabSelectionHandler",
      "javax.swing.plaf.basic.BasicTableHeaderUI$MouseInputHandler",
      "javax.swing.plaf.basic.BasicTableUI$FocusHandler", "javax.swing.plaf.basic.BasicTableUI$KeyHandler",
      "javax.swing.plaf.basic.BasicTableUI$MouseInputHandler", "javax.swing.plaf.basic.BasicTextUI$BasicCaret",
      "javax.swing.plaf.basic.BasicToolBarUI$DockingListener", "javax.swing.plaf.basic.BasicToolBarUI$FrameListener",
      "javax.swing.plaf.basic.BasicToolBarUI$PropertyListener",
      "javax.swing.plaf.basic.BasicToolBarUI$ToolBarContListener",
      "javax.swing.plaf.basic.BasicToolBarUI$ToolBarFocusListener",
      "javax.swing.plaf.basic.BasicTreeUI$CellEditorHandler", "javax.swing.plaf.basic.BasicTreeUI$ComponentHandler",
      "javax.swing.plaf.basic.BasicTreeUI$FocusHandler", "javax.swing.plaf.basic.BasicTreeUI$KeyHandler",
      "javax.swing.plaf.basic.BasicTreeUI$MouseHandler", "javax.swing.plaf.basic.BasicTreeUI$MouseInputHandler",
      "javax.swing.plaf.basic.BasicTreeUI$PropertyChangeHandler",
      "javax.swing.plaf.basic.BasicTreeUI$SelectionModelPropertyChangeHandler",
      "javax.swing.plaf.basic.BasicTreeUI$TreeCancelEditingAction",
      "javax.swing.plaf.basic.BasicTreeUI$TreeExpansionHandler", "javax.swing.plaf.basic.BasicTreeUI$TreeHomeAction",
      "javax.swing.plaf.basic.BasicTreeUI$TreeIncrementAction", "javax.swing.plaf.basic.BasicTreeUI$TreeModelHandler",
      "javax.swing.plaf.basic.BasicTreeUI$TreePageAction", "javax.swing.plaf.basic.BasicTreeUI$TreeSelectionHandler",
      "javax.swing.plaf.basic.BasicTreeUI$TreeToggleAction", "javax.swing.plaf.basic.BasicTreeUI$TreeTraverseAction",
      "javax.swing.plaf.metal.MetalComboBoxEditor", "javax.swing.plaf.metal.MetalComboBoxEditor$UIResource",
      "javax.swing.plaf.metal.MetalComboBoxUI$MetalPropertyChangeListener",
      "javax.swing.plaf.metal.MetalFileChooserUI$DirectoryComboBoxAction",
      "javax.swing.plaf.metal.MetalFileChooserUI$FilterComboBoxModel",
      "javax.swing.plaf.metal.MetalFileChooserUI$SingleClickListener", "javax.swing.plaf.metal.MetalLabelUI",
      "javax.swing.plaf.metal.MetalRootPaneUI", "javax.swing.plaf.metal.MetalSliderUI$MetalPropertyListener",
      "javax.swing.plaf.metal.MetalToolBarUI$MetalContainerListener",
      "javax.swing.plaf.metal.MetalToolBarUI$MetalDockingListener",
      "javax.swing.plaf.metal.MetalToolBarUI$MetalRolloverListener",
      "javax.swing.plaf.synth.SynthSliderUI$SynthTrackListener", "javax.swing.table.DefaultTableColumnModel",
      "javax.swing.table.JTableHeader", "javax.swing.text.DefaultCaret",
      "javax.swing.text.DefaultEditorKit$BeepAction", "javax.swing.text.DefaultEditorKit$CopyAction",
      "javax.swing.text.DefaultEditorKit$CutAction", "javax.swing.text.DefaultEditorKit$DefaultKeyTypedAction",
      "javax.swing.text.DefaultEditorKit$InsertBreakAction", "javax.swing.text.DefaultEditorKit$InsertContentAction",
      "javax.swing.text.DefaultEditorKit$InsertTabAction", "javax.swing.text.DefaultEditorKit$PasteAction",
      "javax.swing.text.JTextComponent$AccessibleJTextComponent", "javax.swing.text.StyledEditorKit$AlignmentAction",
      "javax.swing.text.StyledEditorKit$BoldAction", "javax.swing.text.StyledEditorKit$FontFamilyAction",
      "javax.swing.text.StyledEditorKit$FontSizeAction", "javax.swing.text.StyledEditorKit$ForegroundAction",
      "javax.swing.text.StyledEditorKit$ItalicAction", "javax.swing.text.StyledEditorKit$StyledTextAction",
      "javax.swing.text.StyledEditorKit$UnderlineAction", "javax.swing.text.TextAction",
      "javax.swing.text.html.FormView", "javax.swing.text.html.FormView$MouseEventListener",
      "javax.swing.text.html.HTMLEditorKit$HTMLTextAction", "javax.swing.text.html.HTMLEditorKit$InsertHTMLTextAction",
      "javax.swing.text.html.HTMLEditorKit$LinkController", "javax.swing.tree.DefaultTreeCellEditor",
      "javax.swing.undo.UndoManager" };
  public final static HashSet<String> listenerClassNameSet = new HashSet<String>(Arrays.asList(listenerClassNameList));

  public final static String[] eventHandlerMethodNameList = { "actionPerformed", "adjustmentValueChanged",
      "ancestorAdded", "ancestorMoved", "ancestorRemoved", "ancestorResized", "caretPositionChanged", "caretUpdate",
      "changedUpdate", "columnAdded", "columnMarginChanged", "columnMoved", "columnRemoved", "columnSelectionChanged",
      "componentAdded", "componentHidden", "componentMoved", "componentRemoved", "componentResized", "componentShown",
      "contentsChanged", "dragDropEnd", "dragEnter", "dragExit", "dragGestureRecognized", "dragMouseMoved", "dragOver",
      "drop", "dropActionChanged", "editingCanceled", "editingStopped", "eventDispatched", "flavorsChanged",
      "focusGained", "focusLost", "hierarchyChanged", "hyperlinkUpdate", "inputMethodTextChanged", "insertUpdate",
      "internalFrameActivated", "internalFrameClosed", "internalFrameClosing", "internalFrameDeactivated",
      "internalFrameDeiconified", "internalFrameIconified", "internalFrameOpened", "intervalAdded", "intervalRemoved",
      "itemStateChanged", "keyPressed", "keyReleased", "keyTyped", "menuCanceled", "menuDeselected",
      "menuDragMouseDragged", "menuDragMouseEntered", "menuDragMouseExited", "menuDragMouseReleased", "menuKeyPressed",
      "menuKeyReleased", "menuKeyTyped", "menuSelected", "mouseClicked", "mouseDragged", "mouseEntered", "mouseExited",
      "mouseMoved", "mousePressed", "mouseReleased", "mouseWheelMoved", "popupMenuCanceled",
      "popupMenuWillBecomeInvisible", "popupMenuWillBecomeVisible", "removeUpdate", "sorterChanged", "stateChanged",
      "tableChanged", "textValueChanged", "treeCollapsed", "treeExpanded", "treeNodesChanged", "treeNodesInserted",
      "treeNodesRemoved", "treeStructureChanged", "treeWillCollapse", "treeWillExpand", "undoableEditHappened",
      "valueChanged", "windowActivated", "windowClosed", "windowClosing", "windowDeactivated", "windowDeiconified",
      "windowGainedFocus", "windowIconified", "windowLostFocus", "windowOpened", "windowStateChanged" };
  public final static HashSet<String> eventHandlerMethodNameSet = new HashSet<String>(
      Arrays.asList(eventHandlerMethodNameList));

  private static Set<String> actionListenerNameSet = new HashSet<String>(actionListenerNameList);

  public static String getComponentTitle(Accessible component) {
    if (component == null)
      return "null";

    AccessibleContext aContext = component.getAccessibleContext();
    if (aContext == null) {
      return "null(noAccessibleContext)";
    }

    // if JTabbedPane, concatenate tab strings
    if (component instanceof JTabbedPane) {
      JTabbedPane tabPane = (JTabbedPane) component;

      int tabCount = tabPane.getTabCount();
      StringBuilder tabTitles = new StringBuilder();
      for (int i = 0; i < tabCount; i++) {
        tabTitles.append(tabPane.getTitleAt(i));
        tabTitles.append("|");
      }
      return tabTitles.toString();
    }

    String title = null;
    try {
      title = aContext.getAccessibleName();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (title != null && !title.equals(""))
      return title;

    // programmatically call
    title = (String) IOUtil.invokeGetMethod(component, "getName");
    if (title != null && !title.equals(""))
      return title;

    // if bean's name doesn't exist
    title = (String) IOUtil.invokeGetMethod(component, "getTitle");
    if (title != null && !title.equals(""))
      return title;

    // 2011-03-17 deleted since texts in textfields are changed frequently.
    // title = (String) invokeGetMethod(component, "getText");
    // if (title != null && !title.equals(""))
    // return title;

    if ("".equals(component.getClass().getSimpleName())) // if inner class
    {
      title = component.getClass().getSuperclass().getSimpleName() + "(inner class)";
      return title;
    }

    title = JFCUtil.getIconName(component); // if icon is exist

    if (title != null && !title.equals(""))
      return title;

    if (component instanceof Component) { // if glass pane
      Container parent = ((Component) component).getParent();
      // if glass pane
      if (parent instanceof JRootPane && ((JRootPane) parent).getGlassPane().equals(component)) {
        title = "glass pane";
        return title;
      }

      // if content pane
      if (parent instanceof JLayeredPane
          && ((JLayeredPane) parent).getLayer((Component) component) == JLayeredPane.FRAME_CONTENT_LAYER) {
        Container grandParent = parent.getParent();
        if (grandParent instanceof JRootPane && ((JRootPane) grandParent).getContentPane().equals(component)) {
          title = "content pane";
          return title;
        }
      }
      title = component.getClass().getSimpleName();
      if ("".equals(title)) // if inner class
      {
        title = component.getClass().getSuperclass().getSimpleName() + "(inner class)";
        return title;
      }

      if (component instanceof Box) {
        Box boxComp = (Box) component;
        Border borderComp = boxComp.getBorder();
        if (borderComp instanceof TitledBorder) {
          return ((TitledBorder) borderComp).getTitle();
        }
      }

      // title[index]
      Container parentComponent = ((Component) component).getParent();
      if (parentComponent != null) {
        Component[] components = parentComponent.getComponents();
        int index = 0;
        for (Component comp : components) {
          if (comp != null && comp.equals(component)) {
            title = String.format("%s[%d]", title, index);
            return title;
          }
          index++;
        }
      }
      return title;
      // 2011-03-17 deleted since x,y,width, and height can change by look&feel
      // title = String.format("%s[%d,%d][%dx%d]", title, ((Component) component).getX(), ((Component)
      // component).getY(), ((Component) component)
      // .getWidth(), ((Component) component).getHeight());
      // return title;
    }

    // TODO identify other components' readable signature
    return "unknown(" + component.getClass().getName() + ")";
  }

  public static String getComponentClassName(Accessible component) {
    return component.getClass().getName();
  }

  public static String getIconName(Accessible component) {
    String iconName = null;
    Object result = IOUtil.invokeGetMethod(component, "getIcon");
    if (result == null)
      return null;
    result = IOUtil.invokeGetMethod(result, "getDescription");
    if (result == null)
      return null;
    iconName = result.toString();
    if (iconName != null) {
      int pivot = iconName.lastIndexOf("/");
      return iconName.substring(pivot + 1);
    }

    return iconName;
  }

  public static String getLayoutName(Accessible component) {
    LayoutManager layoutMgr = ((Container) component).getLayout();
    if (layoutMgr == null)
      return "null";
    String layoutName = layoutMgr.getClass().getName();
    return layoutName;
  }

  public static int getComponentIndex(Accessible component) {
    if (!(component instanceof Component))
      return 0;
    Container parentComponent = ((Component) component).getParent();
    if (parentComponent != null) {
      Component[] components = parentComponent.getComponents();
      int index = 0;
      for (Component comp : components) {
        if (comp != null && comp.equals(component)) {
          return index;
        }
        index++;
      }
    }
    return 0;
  }

  public static <T> String getListenerNames(Class<T> listenerType, Object value) {
    T[] listeners = (T[]) value;
    HashSet<String> listenerNames = new HashSet<String>();
    String result = "";

    for (T listener : listeners) {
      String name = listener.getClass().getName();
      listenerNames.add(name);
    }

    ArrayList<String> list = new ArrayList<String>(listenerNames);
    Collections.sort(list);
    for (String name : list)
      result += name + ";";

    return result;
  }

  public static List<Object> getAccessibleChilds(Accessible parent) {
    List<Object> result = new ArrayList<Object>();
    if (parent != null) {
      AccessibleContext aContext = parent.getAccessibleContext();
      if (aContext != null) {
        int count = aContext.getAccessibleChildrenCount();
        for (int i = 0; i < count; i++) {
          result.add(aContext.getAccessibleChild(i));
        }
      }
    }
    return result;
  }

  public static boolean isModalBlocked(Window window) {

    try {
      // use trick(using reflection) to get the blocked window info.
      Method isModalBlockedMethod;
      isModalBlockedMethod = Window.class.getDeclaredMethod("isModalBlocked", new Class[0]);
      isModalBlockedMethod.setAccessible(true);
      boolean result = (Boolean) isModalBlockedMethod.invoke(window, new Object[0]);
      return result;

    } catch (Exception e) {
      e.printStackTrace();
      return false;

    }

  }

  public static void expandAll(JTree tree) {
    int row = 0;
    while (row < tree.getRowCount()) {
      tree.expandRow(row);
      row++;
    }
  }

  public static void expandToLast(JTree tree) {
    // expand to the last leaf from the root
    DefaultMutableTreeNode root;
    root = (DefaultMutableTreeNode) tree.getModel().getRoot();
    tree.scrollPathToVisible(new TreePath(root.getLastLeaf().getPath()));
  }

  public static void collapseAll(JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 0) {
      tree.collapseRow(row);
      row--;
    }
  }

  public static void waitForEventIdle() {
    // the idea is from google testing blog.

    if (EventQueue.isDispatchThread())
      return;

    ObjRef probe = new ObjRef(false);

    // insert probe
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // insert prove again
    probe.setRef(false);
    EventQueue.invokeLater(new RunnableMethod(probe) {
      @Override
      public void run() {
        ((ObjRef) getInputObj()).setRef(true);
      }

    });

    // wait until probe is set to true
    while (true) {
      if ((Boolean) probe.getRef())
        break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // JXTable display properties
  // center column header text
  @SuppressWarnings("serial")
  public static JXTable createXTable() {
    JXTable table = new JXTable() {

      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel) {

          @Override
          public void updateUI() {
            super.updateUI();
            // need to do in updateUI to survive toggling of LAF
            if (getDefaultRenderer() instanceof JLabel) {
              ((JLabel) getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

            }
          }
        };
      }
    };
    return table;
  }

  public static GUIModel shallowTreeCopy(GUIModel model) {
    GUIModel newModel = (GUIModel) model.clone();
    int childCount = model.getChildCount();
    for (int i = 0; i < childCount; i++) {
      newModel.add(shallowTreeCopy((GUIModel) model.getChildAt(i)));
    }
    return newModel;
  }

  public static void saveFullScreenImage(String filename, String fileType) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    Dimension screenSize = toolkit.getScreenSize();
    Rectangle screenRect = new Rectangle(screenSize);
    try {
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRect);

      ImageIO.write(image, fileType, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    } catch (AWTException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    }
  }

  public static void saveWindowsImage(GUIModel guiModel, String filename, String fileType) {
    int childCount = guiModel.getChildCount();
    Rectangle screenRect = null;
    for (int i = 0; i < childCount; i++) {
      WindowModel windowModel = (WindowModel) guiModel.getChildAt(i).getUserObject();
      Window window = (Window) windowModel.getRef();
      if (window == null || !window.isShowing())
        continue;
      Point point = new Point(0, 0);
      SwingUtilities.convertPointToScreen(point, window);

      if (screenRect == null)
        screenRect = new Rectangle(point, window.getSize());
      else
        screenRect.add(new Rectangle(point, window.getSize()));

    }
    try {
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRect);
      ImageIO.write(image, fileType, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    } catch (AWTException e) {
      e.printStackTrace();
      TestLogger.log.error(e.getMessage());
    }
  }

  public static void saveComponentImage(final Component component, final String filename, final String fileType) {
    if (!component.isShowing())
      return;
    // Point point = new Point(0, 0);
    // SwingUtilities.convertPointToScreen(point, component);
    // Rectangle screenRect = new Rectangle(point, component.getSize());
    //
    // try {
    // Robot robot = new Robot();
    // BufferedImage image = robot.createScreenCapture(screenRect);
    // ImageIO.write(image, fileType, new File(filename));
    // } catch (IOException e) {
    // e.printStackTrace();
    // TestLogger.log.error(e.getMessage());
    // } catch (AWTException e) {
    // e.printStackTrace();
    // TestLogger.log.error(e.getMessage());
    // }
    if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0)
      return;

    // code below is invoked by the event queue because of the concurrency problem.
    try {
      EventQueue.invokeAndWait(new Runnable() {
        @Override
        public void run() {

          BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
              BufferedImage.TYPE_INT_RGB);
          // switch off double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);

          component.paint(image.getGraphics());

          // switch on double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
          try {
            ImageIO.write(image, fileType, new File(filename));
          } catch (IOException e) {
            e.printStackTrace();
          }

        }

      });
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }

  }

  public static BufferedImage loadImage(String filename) {
    try {
      BufferedImage image = ImageIO.read(new File(filename));
      return image;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getComponentRole(Accessible component) {
    if (component == null)
      return "null";

    AccessibleContext aContext = component.getAccessibleContext();
    if (aContext == null) {
      return "null(noAccessibleContext)";
    }

    AccessibleRole aRole = aContext.getAccessibleRole();

    if (aRole == null) {
      return "null(noAccessibleRole)";
    }
    return aRole.toDisplayString(Locale.ENGLISH);
  }

  public static AccessibleContext getAccessibleContext(ComponentModel model) {
    if (model == null)
      return null;
    Accessible accessible = (Accessible) model.getRef();
    if (accessible == null)
      return null;
    AccessibleContext aContext = accessible.getAccessibleContext();
    if (aContext == null)
      return null;
    return aContext;
  }

  public static byte[] getImageBuffer(final Component component) {
    if (!component.isShowing())
      return new byte[0];
    if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0)
      return new byte[0];

    final Object[] result = new Object[1];
    result[0] = new byte[0];

    // code below is invoked by the event queue because of the concurrency problem.
    try {
      EventQueue.invokeAndWait(new Runnable() {
        @Override
        public void run() {

          BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
              BufferedImage.TYPE_INT_RGB);
          // switch off double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);

          component.paint(image.getGraphics());

          // switch on double buffering
          RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
          try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            result[0] = imageInByte;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      });
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }
    return (byte[]) result[0];
  }

  public static Set<String> getEventHandlerSet(Component comp, Set<String> appClassNameSet) {
    HashSet<String> eventHandlerSet = new HashSet<String>();

    Method[] methods = comp.getClass().getMethods();

    for (Method method : methods) {

      String methodName = method.getName();

      if (!actionListenerNameSet.contains(methodName))
        continue;

      int paramLength = method.getParameterTypes().length;
      if (paramLength > 0)
        continue;

      Object[] argments = new Object[0];
      try {
        Object rtnObj = method.invoke(comp, argments);

        Set<String> eventHandlerNames = getEventHandlerNames(methodName, rtnObj, appClassNameSet);
        eventHandlerSet.addAll(eventHandlerNames);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return eventHandlerSet;
  }

  private static Set<String> getEventHandlerNames(String key, Object value, Set<String> appClassNameSet) {
    if ("getActionListeners".equals(key))
      return JFCUtil.getListenerMethodNames(ActionListener.class, value, appClassNameSet);
    else if ("getAncestorListeners".equals(key))
      return JFCUtil.getListenerMethodNames(AncestorListener.class, value, appClassNameSet);
    else if ("getWindowListeners".equals(key))
      return JFCUtil.getListenerMethodNames(WindowListener.class, value, appClassNameSet);
    else if ("getComponentListeners".equals(key))
      return JFCUtil.getListenerMethodNames(ComponentListener.class, value, appClassNameSet);
    else if ("getKeyListeners".equals(key))
      return JFCUtil.getListenerMethodNames(KeyListener.class, value, appClassNameSet);
    else if ("getMouseListeners".equals(key))
      return JFCUtil.getListenerMethodNames(MouseListener.class, value, appClassNameSet);
    else if ("getMouseMotionListeners".equals(key))
      return JFCUtil.getListenerMethodNames(MouseMotionListener.class, value, appClassNameSet);
    else if ("getMouseWheelListeners".equals(key))
      return JFCUtil.getListenerMethodNames(MouseWheelListener.class, value, appClassNameSet);
    else if ("getPropertyChangeListeners".equals(key))
      return JFCUtil.getListenerMethodNames(PropertyChangeListener.class, value, appClassNameSet);
    else if ("getVetoableChangeListeners".equals(key))
      return JFCUtil.getListenerMethodNames(VetoableChangeListener.class, value, appClassNameSet);
    else if ("getContainerListeners".equals(key))
      return JFCUtil.getListenerMethodNames(ContainerListener.class, value, appClassNameSet);
    else if ("getFocusListeners".equals(key))
      return JFCUtil.getListenerMethodNames(FocusListener.class, value, appClassNameSet);
    else if ("getHierarchyBoundsListeners".equals(key))
      return JFCUtil.getListenerMethodNames(HierarchyBoundsListener.class, value, appClassNameSet);
    else if ("getHierarchyBoundsListeners".equals(key))
      return JFCUtil.getListenerMethodNames(HierarchyBoundsListener.class, value, appClassNameSet);
    else if ("getInputMethodListeners".equals(key))
      return JFCUtil.getListenerMethodNames(InputMethodListener.class, value, appClassNameSet);
    return new HashSet<String>();
  }

  public static <T> Set<String> getListenerMethodNames(Class<T> listenerType, Object value, Set<String> appClassNameSet) {
    Set<String> result = new HashSet<String>();
    T[] listeners = (T[]) value;
    for (T listener : listeners) {
      Class<? extends Object> klass = listener.getClass();
      String className = IOUtil.getFullClassName(klass);
      if (!appClassNameSet.contains(className))
        continue;
      Method[] methods = listener.getClass().getMethods();
      for (Method method : methods) {
        String methodName = method.getName();

        if (eventHandlerMethodNameSet.contains(methodName)) {
          String methodSig = IOUtil.getMethodSignature(className, method);
          result.add(methodSig);
        }
      }
    }

    return result;
  }

  // public static LinkedList<EventModel> getEventsWithGroup(GUIModel rootNode) {
  // return getEventsWithGroup(rootNode, false);
  // }

  // public static LinkedList<EventModel> getEventsWithGroup(GUIModel rootNode, boolean includeDisabledWidget) {
  // LinkedList<EventModel> result = new LinkedList<EventModel>();
  // HashSet<EventModel> temp = new HashSet<EventModel>();
  // // get event node
  //
  // for (Enumeration<GUIModel> enumurator = rootNode.depthFirstEnumeration(); enumurator.hasMoreElements();) {
  // GUIModel node = enumurator.nextElement();
  // Object userObj = node.getUserObject();
  // if (userObj instanceof ComponentModel) {
  // ComponentModel element = (ComponentModel) userObj;
  //
  // boolean isEnabled = !"false".equals(element.get("enabled"));
  // boolean isVisible = !"false".equals(element.get("visible"));
  // if (includeDisabledWidget || (isVisible && isEnabled)) {
  // List<EventModel> eventList = element.getEventList();
  //
  // if ("radio button".equals(element.get("role"))) {
  // Object surroundingComp = node.getParent().getUserObject();
  // if (surroundingComp instanceof ComponentModel) {
  // element.put("group", ((ComponentModel) surroundingComp).get("id"));
  // }
  // } else if ("slider".equals(element.get("role"))) {
  // element.put("group", element.get("id"));
  // } else if ("slider".equals(element.get("role"))) {
  // element.put("group", element.get("id"));
  // } else if ("text".equals(element.get("role"))) {
  // element.put("group", element.get("id"));
  // }
  // result.addAll(eventList);
  // }
  // }
  // }
  // // expand an event node if the node is container event node.
  // for (int i = 0; i < result.size();) {
  // EventModel modelEvent = result.get(i);
  // if (modelEvent.isContainer()) {
  // result.remove(i);
  //
  // List<EventModel> eventList = modelEvent.getChildren();
  // for (EventModel e : eventList) {
  // if (!temp.contains(e)) {
  // result.add(e);
  // temp.add(e);
  // }
  // }
  // } else
  // i++;
  // }
  // return result;
  // }

  public static String getComponentGroup(GUIModel node, ComponentModel componentModel) {
    if (node == null || componentModel == null)
      return "";
    if ("radio button".equals(componentModel.get("role"))) {
      Object surroundingComp = node.getParent().getUserObject();
      if (surroundingComp instanceof ComponentModel) {
        return ((ComponentModel) surroundingComp).get("id");
      }
    } else if ("combo box".equals(componentModel.get("role"))) {
      return componentModel.get("id");
    } else if ("slider".equals(componentModel.get("role"))) {
      return componentModel.get("id");
    } else if ("text".equals(componentModel.get("role"))) {
      return componentModel.get("id");
    }
    return "";
  }
}
