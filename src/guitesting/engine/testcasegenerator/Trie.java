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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie<T> {

  private Map<T, TreeNode> roots = new HashMap<T, TreeNode>();

  public Trie() {
  }

  public void add(List<T> item) {
    TreeNode<T> currentNode = null;
    int itemSize = item.size();

    T element = item.get(0);
    if (!roots.containsKey(element)) {
      roots.put(element, new TreeNode(element));
    }

    currentNode = roots.get(element);

    for (int i = 1; i < itemSize; i++) {
      element = item.get(i);
      if (currentNode.getChild(element) == null) {
        currentNode.addChild(new TreeNode(element));
      }
      currentNode = currentNode.getChild(element);
    }
    currentNode.addItem(item);
  }

  public boolean addNonDuplicate(List<T> item) {
    TreeNode<T> currentNode = null;
    int itemSize = item.size();

    T element = item.get(0);
    if (!roots.containsKey(element)) {
      roots.put(element, new TreeNode(element));
    }

    currentNode = roots.get(element);

    for (int i = 1; i < itemSize; i++) {
      element = item.get(i);
      if (currentNode.getChild(element) == null) {
        currentNode.addChild(new TreeNode(element));
      }
      currentNode = currentNode.getChild(element);
    }
    if (currentNode.getItemCount() == 0) {
      currentNode.addItem(item);
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    return roots.toString();
  }

  public boolean contains(List<T> argPrefix) {
    TreeNode node = getNode(argPrefix);
    return node != null;
  }

  public TreeNode getNode(List<T> item) {
    TreeNode<T> currentNode = roots.get(item.get(0));

    int itemSize = item.size();
    for (int i = 1; i < itemSize && currentNode != null; i++) {
      currentNode = currentNode.getChild(item.get(i));

      if (currentNode == null) {
        return null;
      }
    }

    return currentNode;
  }

  public int countLeafItems() {
    int result = 0;
    for (T key : roots.keySet()) {
      TreeNode root = roots.get(key);

      result += countLeafItems(root);
    }
    return result;

  }

  private int countLeafItems(TreeNode<T> root) {
    int result = 0;
    if (root.isLeaf()) {
      return 1;
    } else {
      for (TreeNode child : root.getChildren()) {
        result += countLeafItems(child);
      }
    }
    return result;
  }

  public void collectLeafItems(List<List<T>> reducedTestCases) {

    for (T key : roots.keySet()) {
      TreeNode root = roots.get(key);

      collectLeafItems(root, reducedTestCases);
    }

  }

  private void collectLeafItems(TreeNode<T> root, List<List<T>> reducedTestCases) {
    if (root.isLeaf()) {
      reducedTestCases.add(root.getItem());
    } else {
      for (TreeNode child : root.getChildren()) {
        collectLeafItems(child, reducedTestCases);
      }
    }
  }

  public int countAllItems() {
    int result = 0;
    for (T key : roots.keySet()) {
      TreeNode root = roots.get(key);

      result += countAllItems(root);
    }
    return result;
  }

  private int countAllItems(TreeNode<T> root) {
    int result = root.getItemCount();

    for (TreeNode<T> child : root.getChildren()) {
      result += countAllItems(child);
    }

    return result;
  }

  public static void main(String[] args) {
    Trie<Integer> test = new Trie<Integer>();
    test.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
    test.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)));
    test.add(new ArrayList<Integer>(Arrays.asList(1, 5, 3, 4, 5)));
    test.add(new ArrayList<Integer>(Arrays.asList(1, 3, 2)));
    test.add(new ArrayList<Integer>(Arrays.asList(1, 3, 2, 9)));
    test.add(new ArrayList<Integer>(Arrays.asList(1, 3, 2, 7)));
    System.out.println(test.countLeafItems());

  }
}
