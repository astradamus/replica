package com.alexanderstrada.replica.space2d

import scala.util.Random

case class Vector2d(x: Double, y: Double) {

  def w = x
  def h = y

  def magnitude = Calc.distance(Vector2d.ZERO, this)

  def sum = x + y
  def map(f: (Double) => Double) = Vector2d(f(x), f(y))

  def offsetRandomly(rangeXY: Double): Vector2d = offsetRandomly(rangeXY, rangeXY)
  def offsetRandomly(rangeX: Double, rangeY: Double): Vector2d =
    this + (Vector2d.random(rangeX, rangeY) - (Vector2d(rangeX, rangeY) / 2))

  def +(o: Vector2d) = Vector2d(x+o.x, y+o.y)
  def -(o: Vector2d) = Vector2d(x-o.x, y-o.y)
  def *(o: Vector2d) = Vector2d(x*o.x, y*o.y)
  def /(o: Vector2d) = Vector2d(x/o.x, y/o.y)

  def +(d: Double) = Vector2d(x+d, y+d)
  def -(d: Double) = Vector2d(x-d, y-d)
  def *(d: Double) = Vector2d(x*d, y*d)
  def /(d: Double) = Vector2d(x/d, y/d)

  def +(x: Double, y: Double) = Vector2d(this.x+x, this.y+y)
  def -(x: Double, y: Double) = Vector2d(this.x-x, this.y-y)
  def *(x: Double, y: Double) = Vector2d(this.x*x, this.y*y)
  def /(x: Double, y: Double) = Vector2d(this.x/x, this.y/y)

  def toUnitVector = this / magnitude
}

object Vector2d {
  val ZERO = Vector2d(0, 0)
  def random(maxX: Double, maxY: Double) = Vector2d(Random.nextDouble() * maxX,
                                                    Random.nextDouble() * maxY)

  def withBothAs(d: Double) = Vector2d(d, d)
}
