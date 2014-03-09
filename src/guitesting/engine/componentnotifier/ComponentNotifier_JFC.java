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
package guitesting.engine.componentnotifier;

import guitesting.util.RunnableMethod;
import guitesting.util.TestProperty;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class ComponentNotifier_JFC extends ComponentNotifier {
  Component oldComponent = null;
  long lastTime = 0;
  int interval = TestProperty.propertyStore.getInt("guitester.componentnotifier.interval", 300);

  public void addNotifier(final RunnableMethod runnableMethod) {
    if (runnableMethod == null)
      return;

    Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueue() {
      @Override
      protected void dispatchEvent(AWTEvent event) {

        if (event instanceof MouseEvent) {
          long time = System.currentTimeMillis();
          if (time - lastTime > interval) {
            lastTime = time;
            Component component = ((MouseEvent) event).getComponent();
            if (component != null) {
              component = SwingUtilities.getDeepestComponentAt(component, ((MouseEvent) event).getX(),
                  ((MouseEvent) event).getY());
              if (oldComponent != component) {
                runnableMethod.setUserObj(component);
                runnableMethod.run();
                oldComponent = component;
              }
            }
          }
        }
        super.dispatchEvent(event);
      }
    });

  }

  @Override
  public BufferedImage getComponentImage(Object compObj) {
    BufferedImage image = null;
    if (compObj instanceof Component) {
      Component component = (Component) compObj;

      if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0)
        return null;
      else {
        image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        // switch off double buffering
        RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);

        component.paint(image.getGraphics());

        // switch on double buffering
        RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
      }
    }
    return image;
  }

}
