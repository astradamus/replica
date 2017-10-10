package com.alexanderstrada.replica.space2d

import com.alexanderstrada.replica.space2d.QuadTree.QuadTreeItem
import org.scalatest._

class QuadTreeTest extends FlatSpec with Matchers {

  class TestItem(val toMaxBoundingRect: Rect) extends QuadTreeItem {
    override def toBoundingRect = toMaxBoundingRect
  }

  val bounds = Rect.mkFromTopLeft(0, 0, 1024, 1024)

  val sub = bounds.subdivide
  val tl = sub(0)
  val tr = sub(1)
  val bl = sub(2)
  val br = sub(3)

  val qsub = tl.subdivide
  val qtl = qsub(0)
  val qtr = qsub(1)
  val qbl = qsub(2)
  val qbr = qsub(3)

  val b_all = new TestItem(bounds)
  val b_semi = new TestItem(bounds)
  val b_tl = new TestItem(tl)
  val b_tr = new TestItem(tr)
  val b_bl = new TestItem(bl)
  val b_br = new TestItem(br)

  val b_qtl = new TestItem(qtl)
  val b_qtr = new TestItem(qtr)
  val b_qbl = new TestItem(qbl)
  val b_qbr = new TestItem(qbr)

  val b_qtl2 = new TestItem(qtl)

  val tree = QuadTree.empty[TestItem](bounds, 5, 5)

  "A QuadTree" should "start with no children and no items" in {
    tree.isEmpty should be (true)
  }

  it should "not split before it overfills" in {
    tree insert b_all
    tree insert b_tl
    tree insert b_tr
    tree insert b_bl
    tree insert b_br
    tree.totalItems should be (5)
    tree.childCount should be (0)
  }

  it should "split when it overfills" in {
    tree insert b_semi
    tree.childCount should be (4)
  }

  it should "return 3 items for rect 'topleft'" in {
    val lookup = tree.query(b_tl.toMaxBoundingRect)
    lookup.size should be (3)
  }

   it should "return all items for rect 'all'" in {
    val lookup = tree.query(b_all.toMaxBoundingRect)
    lookup.size should be (6)
  }

  it should "not split again before a sub-node overfills" in {
    tree insert b_qtl
    tree insert b_qtr
    tree insert b_qbl
    tree insert b_qbr
    tree.totalItems should be (10)
    tree.childCount should be (4)
  }

  it should "split again when a sub-node overfills" in {
    tree.childCount should be (4)
    tree insert b_qtl2
    tree.totalItems should be (11)
    tree.childCount should be (8)
  }
}
