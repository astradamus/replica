package com.alexanderstrada.replica.space2d

import scala.util.Random

case class Rect(
    pos: Vector2d = Vector2d.ZERO,
    size: Vector2d = Vector2d.ZERO) {

  def this(x: Double, y: Double, w: Double, h: Double) = this(Vector2d(x, y), Vector2d(w, h))

  def center: Vector2d = pos

  def x: Double = pos.x
  def y: Double = pos.y
  def w: Double = size.w
  def h: Double = size.h

  def left: Double = x - w/2
  def top: Double = y - h/2
  def right: Double = x + w/2
  def bottom: Double = y + h/2

  def topLeft: Vector2d     = Vector2d(left, top)
  def topRight: Vector2d    = Vector2d(right, top)
  def bottomLeft: Vector2d  = Vector2d(left, bottom)
  def bottomRight: Vector2d = Vector2d(right, bottom)

  def random: Vector2d = Vector2d(left + Random.nextDouble() * w,
                        top + Random.nextDouble() * h)

  def contains(p: Vector2d): Boolean = (p.x >= left && p.x < right) && (p.y >= top && p.y < bottom)
  def contains(r: Rect): Boolean = (left <= r.left) && (top <= r.top) && (right >= r.right) && (bottom >= r.bottom)
  def intersects(r: Rect): Boolean = (r.left <= right && r.right >= left) && (r.top <= bottom && r.bottom >= top)

  def offset(o: Vector2d): Rect = Rect(pos + o, size)

  def scale(d: Double): Rect = Rect(pos, size*d)

  def subdivide: Seq[Rect] = {
    val divSize = size / 2
    val topLeftDivOrigin =  topLeft + ((center - topLeft) / 2)
    Seq(Rect(topLeftDivOrigin, divSize),
        Rect(topLeftDivOrigin + (divSize.w, 0), divSize),
        Rect(topLeftDivOrigin + (0, divSize.h), divSize),
        Rect(topLeftDivOrigin + divSize, divSize))
  }
}

object Rect {
  def apply(x: Double, y: Double, w: Double, h: Double): Rect = apply(Vector2d(x, y), Vector2d(w, h))
  def mkFromTopLeft(left: Double, top: Double, w: Double, h: Double): Rect = apply(left + w/2, top + h/2, w, h)
}
