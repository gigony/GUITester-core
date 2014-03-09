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

import guitesting.model.PropertyModel;
import guitesting.util.JFCUtil;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

@SuppressWarnings("serial")
public class WindowPropertyTab extends JXPanel {
  GUITesterFrame guiTesterFrame;
  PropertyModel property = new PropertyModel();

  JXTable propertyTable;
  JButton saveButton;

  public WindowPropertyTab(GUITesterFrame guiTesterFrame) {
    this.guiTesterFrame = guiTesterFrame;
    initComponents();
    configureComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    // add property table
    propertyTable = JFCUtil.createXTable();
    propertyTable.setName("Window");
    JScrollPane scrollPane = new JScrollPane(propertyTable);
    add(scrollPane);

    // add control buttons
    JPanel controlPanel = new JPanel();
    saveButton = new JButton("save property");
    controlPanel.add(saveButton);
    add(controlPanel, BorderLayout.SOUTH);

  }

  private void configureComponents() {
    propertyTable.setColumnControlVisible(true);
    propertyTable.setShowGrid(false, false); // replace grid lines with striping
    propertyTable.addHighlighter(HighlighterFactory.createSimpleStriping());
    propertyTable.setVisibleRowCount(10); // initialize preferred size for table's viewable area
    CustomColumnFactory factory = new CustomColumnFactory(); // create and configure a custom column factory
    propertyTable.setColumnFactory(factory); // set the factory before setting the table model
    propertyTable.setModel(property);
    propertyTable.setEditable(true);
    propertyTable.setSortable(false);
    propertyTable.addMouseListener(new MouseListener() {
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
              // remove properties from propertyTable
              int removeIndexes[] = propertyTable.getSelectedRows();
              Arrays.sort(removeIndexes);
              // delete rows
              for (int i = removeIndexes.length - 1; i >= 0; i--)
                propertyTable.getModel().setValueAt("", removeIndexes[i], 1);
            }
          });
          popup.add(menu);
          popup.show(propertyTable, e.getX(), e.getY());
        }
      }
    });

    // add action to buttons

    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        property.saveToRefMap();
        JOptionPane.showMessageDialog(WindowPropertyTab.this, "Save Succeed!");
      }
    });

  }

  public void updateProperty(Map<String, String> properties) {
    property.setproperty(properties);
    propertyTable.packAll();
  }

}
