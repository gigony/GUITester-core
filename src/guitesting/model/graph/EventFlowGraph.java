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
package guitesting.model.graph;

import guitesting.model.traces.ExecutedEventModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class EventFlowGraph implements Serializable {
  private static final long serialVersionUID = 1L;

  HashMap<Node, Node> rootNodes = new HashMap<Node, Node>();

  HashMap<Node, Node> nodes = new HashMap<Node, Node>();

  public class Node implements Serializable {
    private static final long serialVersionUID = 1L;
    ExecutedEventModel event;
    HashMap<Node, Node> edges = new HashMap<Node, Node>();

    public Node(ExecutedEventModel event) {
      this.event = event;
    }

    public ExecutedEventModel getEvent() {
      return event;
    }

    public Iterator<Node> directedToIter() {
      return edges.values().iterator();
    }

    public Collection<Node> directedToSet() {
      return edges.values();
    }

    public boolean isFollowedBy(Node target) {
      return edges.containsKey(target);
    }

    public void setFollowedBy(Node target) {
      if (!isFollowedBy(target)) {
        edges.put(target, target);
      }
    }

    @Override
    public int hashCode() {
      return event.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Node))
        return false;
      return event.equals(((Node) obj).getEvent());
    }
  }

  public void addNode(ExecutedEventModel event) {
    nodes.put(new Node(event), new Node(event));
  }

  public void addEdge(ExecutedEventModel src, ExecutedEventModel dst) {
    if (src == null) {
      Node dstNode = new Node(dst);
      if (!nodes.containsKey(dstNode)) {
        nodes.put(dstNode, dstNode);
        rootNodes.put(dstNode, dstNode);
      }
    } else {
      Node srcNode = new Node(src);
      Node dstNode = new Node(dst);
      if (!nodes.containsKey(srcNode))
        nodes.put(srcNode, srcNode);
      if (!nodes.containsKey(dstNode))
        nodes.put(dstNode, dstNode);

      srcNode = nodes.get(srcNode);
      srcNode.setFollowedBy(nodes.get(dstNode));
    }
  }

  public HashMap<Node, Node> getRootNodes() {
    return rootNodes;
  }

  public HashMap<Node, Node> getNodes() {
    return nodes;
  }

  public Node getNode(ExecutedEventModel event) {
    return nodes.get(new Node(event));
  }

}
