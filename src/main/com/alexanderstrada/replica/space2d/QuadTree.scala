package com.alexanderstrada.replica.space2d

import com.alexanderstrada.replica.space2d.QuadTree.QuadTreeItem

class QuadTree[A <: QuadTreeItem] private (
    val bounds: Rect,
    level: Int,
    itemLimit: Int,
    levelLimit: Int,
    parent: Option[QuadTree[A]] = None) {

  private var items = Set.empty[A]
  private var nodes = Seq.empty[QuadTree[A]]

  /** Return true if this node is empty and has no descendants.*/
  def isEmpty: Boolean = items.isEmpty && nodes.isEmpty

  /** Return the total number of descendants of this node.*/
  def childCount: Int = nodes.map(_.childCount + 1).sum

  /** Return the total number of items stored in this node and all descendants. */
  def totalItems: Int = items.size + nodes.map(_.totalItems).sum

  /** Return a list of nodes ending with the current node, starting with the
    * top-level node in this tree, and including every node in between.*/
  def ancestry: Seq[QuadTree[A]] = parent match {
    case Some(p) => p.ancestry ++ Seq(p)
    case None => Seq()
  }

  /** Add an item to this QuadTree.*/
  def insert(a: A): Unit = findFit(a) match {
    case Some(n) =>
      n.insert(a)
    case None =>
      items += a
      if (shouldSplit) split()
  }

  /** Attempt to remove the given item from this node or the sub-node that contains it.*/
  def remove(a: A): Unit = findFit(a) match {
    case Some(n) =>
      n.remove(a)
    case None if items.contains(a) =>
      items -= a
      maybeCollapse()
    case _ =>
  }

  /** Return all items in this node and its descendants that intersect the given Rect.*/
  def query(r: Rect): Set[A] = (items ++ (findFit(r) match {
    case Some(n) => n.query(r)
    case None => nodes.filter(_.bounds.intersects(r)).flatMap(_.query(r))
  })).filter(_.toBoundingRect.intersects(r))

  /** Attempt to find a child node that can contain the given Rect.*/
  private def findFit(r: Rect): Option[QuadTree[A]] = nodes.find(_.bounds.contains(r))

  /** Attempt to find a child node that can contain the given QuadTreeItem.*/
  private def findFit(a: A): Option[QuadTree[A]] = findFit(a.toMaxBoundingRect)

  /** Return true if this leaf node is eligible to become a branch node. */
  private def shouldSplit = {
    val notYetSplit = nodes.isEmpty
    val itemsMaxed = items.size > itemLimit
    val levelNotMaxed = level < levelLimit

    notYetSplit && itemsMaxed && levelNotMaxed
  }

  /** Turn this leaf node into a branch node and pass down items that fit into any of the new descendants.*/
  private def split(): Unit = {
    nodes = bounds.subdivide.map(sb => new QuadTree[A](sb, level + 1, itemLimit, levelLimit, Some(this)))

    items = items.foldLeft(Set.empty[A])((out, i) => findFit(i) match {
      case None =>
        out + i
      case Some(sub) =>
        sub.insert(i)
        out
    })
  }

  /** Attempt to collapse this node and any collapsable parent nodes into their highest-level uncollapsable parent.
    * A node is collapsable when it and its siblings do not contain enough items to justify their existence.*/
  private def maybeCollapse(): Unit = ancestry.find(_.shouldCollapse) match {
    case Some(p) => p.collapse()
    case None =>
  }

  /** Return true if this branch node is eligible to collapse its sub-nodes and become a leaf node.*/
  private def shouldCollapse = {
    val isSplit = nodes.nonEmpty
    val itemsCanFit = totalItems <= itemLimit

    isSplit && itemsCanFit
  }

  /** Turn this branch node back into a leaf node, reclaiming the contents of all deleted descendants.*/
  private def collapse(): Unit = {
    items ++= collectChildItems
    nodes = Seq.empty[QuadTree[A]]
  }

  /** Return all items contained by all descendants of this node.*/
  def collectChildItems: Set[A] = nodes.flatMap(_.collectChildItems).toSet
}

object QuadTree {
  def empty[A <: QuadTreeItem](bounds: Rect, itemLimit: Int, levelLimit: Int) =
    new QuadTree[A](bounds, 0, itemLimit, levelLimit)

  trait QuadTreeItem {
    def toMaxBoundingRect: Rect
    def toBoundingRect: Rect
  }
}
