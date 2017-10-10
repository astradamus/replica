package com.alexanderstrada.replica.space2d

import scala.util.Random

case class Rect(
    pos: Vector2d = Vector2d.ZERO,
    size: Vector2d = Vector2d.ZERO) {

  def this(x: Double, y: Double, w: Double, h: Double) = this(Vector2d(x, y), Vector2d(w, h))

  def center = pos

  def x = pos.x
  def y = pos.y
  def w = size.w
  def h = size.h

  def left = x - w/2
  def top = y - h/2
  def right = x + w/2
  def bottom = y + h/2

  def topLeft     = Vector2d(left, top)
  def topRight    = Vector2d(right, top)
  def bottomLeft  = Vector2d(left, bottom)
  def bottomRight = Vector2d(right, bottom)

  def random = Vector2d(left + Random.nextDouble() * w,
                        top + Random.nextDouble() * h)

  def contains(p: Vector2d) = (p.x >= left && p.x < right) && (p.y >= top && p.y < bottom)
  def contains(r: Rect) = (left <= r.left) && (top <= r.top) && (right >= r.right) && (bottom >= r.bottom)
  def intersects(r: Rect) = (r.left <= right && r.right >= left) && (r.top <= bottom && r.bottom >= top)

  def offset(o: Vector2d) = Rect(pos + o, size)

  def scale(d: Double) = Rect(pos, size*d)

  def subdivide = {
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
  def mkFromTopLeft(left: Double, top: Double, w: Double, h: Double) = apply(left + w/2, top + h/2, w, h)
}
