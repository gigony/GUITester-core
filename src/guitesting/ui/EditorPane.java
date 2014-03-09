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

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXTitledPanel;

@SuppressWarnings("serial")
public class EditorPane extends JXTitledPanel {
  JTabbedPane tabbedPane;
  GUITesterFrame guiTesterFrame;
  TestCaseTab testCaseTab;
  FilterTab filterTab;
  CustomValueTab customValueTab;

  public EditorPane(GUITesterFrame guiTesterFrame) {
    this.guiTesterFrame = guiTesterFrame;
    initComponents();
    configureComponents();
  }

  private void initComponents() {
    setTitle("Editor");
    tabbedPane = new JTabbedPane();
    JScrollPane scrollPane = new JScrollPane(tabbedPane);
    scrollPane.setAutoscrolls(true);

    testCaseTab = new TestCaseTab(guiTesterFrame);
    tabbedPane.add("Test case", testCaseTab);

    filterTab = new FilterTab(guiTesterFrame);
    tabbedPane.add("Filtering/Substitution", filterTab);

    customValueTab = new CustomValueTab(guiTesterFrame);
    tabbedPane.add("Custom value", customValueTab);

    setPreferredSize(new Dimension(200, 200));

    add(scrollPane);
  }

  private void configureComponents() {
  }

}
