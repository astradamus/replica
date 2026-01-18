package com.alexanderstrada.replica.space2d

object Calc {

  def clamp(d: Double, min: Double, max: Double): Double = Math.max(min, Math.min(d, max))

  def square(d: Double): Double = d * d

  def distance(origin: Vector2d, target: Vector2d): Double =
    Math.sqrt((target - origin).map(square).sum)
}
