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
package guitesting.engine.testcasegenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode<T> {

  private final T element;
  private Map<T, TreeNode> children = new HashMap<T, TreeNode>();
  private List<T> item = null;
  int count = 0;

  public TreeNode(T argChar) {
    element = argChar;
  }

  public boolean addChild(TreeNode argChild) {
    if (children.containsKey(argChild.getElement())) {
      return false;
    }

    children.put((T) argChild.getElement(), argChild);
    return true;
  }

  public void addItem(List<T> item) {
    if (this.item == null) {
      this.item = item;
    }
    count++;
  }

  public boolean containsChildValue(char c) {
    return children.containsKey(Character.toString(c));
  }

  public List<T> getItem() {
    return item;
  }

  public int getItemCount() {
    return count;
  }

  public T getElement() {
    return element;
  }

  public TreeNode getChild(T c) {
    return children.get(c);
  }

  public List<TreeNode> getChildren() {
    List<TreeNode> childrenList = new ArrayList<TreeNode>();
    for (T key : children.keySet()) {
      TreeNode item = children.get(key);
      childrenList.add(item);
    }
    return childrenList;
  }

  public int getChildCount() {
    return children.size();
  }

  public boolean isLeaf() {
    return getChildCount() == 0;
  }
}