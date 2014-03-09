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

import guitesting.engine.componentnotifier.ComponentNotifier;
import guitesting.model.GUIModel;
import guitesting.util.TestProperty;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

@SuppressWarnings("serial")
public class GUITesterFrame extends JFrame {
  GUITester guiTester = null;
  GUIModel model = null;
  JToolBar toolBar = null;
  ComponentTreePane componentTreePane;
  EditorPane editorPane;
  PropertyPane propertyPane;
  PreviewPane previewPane;

  public GUITesterFrame(GUITester guiTester, String string) {
    super(string);

    this.guiTester = guiTester;

    // change UI Look & Feel - this affects programs under test
    // try {
    // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    // } catch (Exception e) {
    // }

    // this frame should not be blocked by application's modal dialog.
    setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);

    // TODO set icon using getResource method

    // add toolbar
    toolBar = createToolBar();
    add(toolBar, BorderLayout.NORTH);

    // add contents
    getContentPane().add(createContents());

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TestProperty.propertyStore.setIfNull("ui.main_frame.width", "1200");
    TestProperty.propertyStore.setIfNull("ui.main_frame.height", "750");
    setPreferredSize(new Dimension(TestProperty.propertyStore.getInt("ui.main_frame.width"),
        TestProperty.propertyStore.getInt("ui.main_frame.height")));
    setResizable(false);
    pack();
    setVisible(true);
  }

  private JToolBar createToolBar() {
    JToolBar toolbar = new JToolBar();
    toolbar.add(new JButton("Test"));

    return toolbar;
  }

  private Component createContents() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JXMultiSplitPane msp = new JXMultiSplitPane();

    // @formatter:off
    TestProperty.propertyStore.setIfNull("ui.layout", "(COLUMN (ROW (LEAF name=componentTree weight=1.0) "
        + "(LEAF name=editor )) " + "(ROW (LEAF name=properties weight=0.8) " + "(LEAF name=preview weight=0.05)))");

    String layoutDef = TestProperty.propertyStore.get("ui.layout");

    // @formatter:on

    MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

    msp.getMultiSplitLayout().setModel(modelRoot);

    componentTreePane = new ComponentTreePane(this);
    editorPane = new EditorPane(this);
    propertyPane = new PropertyPane(this);
    previewPane = new PreviewPane(this);

    msp.add(componentTreePane, "componentTree");
    msp.add(editorPane, "editor");
    msp.add(propertyPane, "properties");
    msp.add(previewPane, "preview");

    // adding a border to the multiplane causes all sorts of issues
    msp.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    panel.add(msp, BorderLayout.CENTER);
    pack();
    return panel;
  }

  /**
   * Updates GUI model using <code>model</code>
   * 
   * @param model
   */
  public void updateGUIModel(GUIModel model) {
    this.model = model;
    componentTreePane.updateGUIModel(model);
  }

  public void setComponentNotifier(ComponentNotifier componentNotifier) {
    if (componentNotifier == null)
      return;

    componentNotifier.setHighlightTarget(componentTreePane);
  }
}
