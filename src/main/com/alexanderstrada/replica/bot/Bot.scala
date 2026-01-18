package com.alexanderstrada.replica.bot

import com.alexanderstrada.replica.space2d.QuadTree.QuadTreeItem
import com.alexanderstrada.replica.space2d.{Rect, Vector2d}

import java.awt.Color

/** Base trait for any 'living' object in the simulation.*/
trait Bot extends QuadTreeItem {

  def pos: Vector2d
  def size: Double
  def toBoundingRect = Rect(pos, Vector2d(size, size))

  def genome: Genome
  val color: Color = genome.color
}
