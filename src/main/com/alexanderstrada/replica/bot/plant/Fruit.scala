package com.alexanderstrada.replica.bot.plant

import com.alexanderstrada.replica.bot.{Bot, Genome}
import com.alexanderstrada.replica.space2d.{Rect, Vector2d}
import com.alexanderstrada.replica.world.World

/** Fruit decays into seeds, which can sprout into a new plant if conditions are
  * right. Will form the basis of the food chain once 'smart' bots are implemented.*/
class Fruit(
    var pos: Vector2d,
    var size: Double,
    val genome: Genome) extends Bot {

  private val originalSize = size

  override def toMaxBoundingRect = Rect(pos, Vector2d.withBothAs(originalSize))

  def tick(w: World) = {
    val s = w.rules.calcDecayedFruitSize(size)
    if (s > 0) size = s
    else sprout(w)
  }

  private def sprout(w: World) = {
    w.delete(this)
    if (!w.arableMap.isVectorTapped(pos))
      w.insert(new Plant(pos, originalSize, genome, w))
  }
}
