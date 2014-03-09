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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.ImagePainter;

@SuppressWarnings("serial")
public class PreviewPane extends JXTitledPanel {
  GUITesterFrame guiTesterFrame;
  BufferedImage image;
  ImagePainter imagePainter;

  public PreviewPane(GUITesterFrame guiTesterFrame) {
    this.guiTesterFrame = guiTesterFrame;
    initComponents();
    configureComponents();

  }

  private void initComponents() {
    setTitle("Preview");

    imagePainter = new ImagePainter();
    imagePainter.setScaleToFit(true);
    JXPanel contentPanel = new JXPanel() {
      @Override
      public void paint(Graphics g) {
        imagePainter.paint((Graphics2D) g, this, getWidth(), getHeight());
      }
    };
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    add(scrollPane);
  }

  private void configureComponents() {

  }

  public void viewComponent(Object component) {
    if (component == null)
      return;
    BufferedImage newImage = GUITester.getInstance().getComponentNotifier().getComponentImage(component);

    image = newImage;

    imagePainter.setImage(image);
    repaint(); // request repainting
  }
}
