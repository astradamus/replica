package com.alexanderstrada.replica.bot.plant

import com.alexanderstrada.replica.bot.{Bot, Genome}
import com.alexanderstrada.replica.space2d.{Rect, Vector2d}
import com.alexanderstrada.replica.world.World

/** A simple, 'dumb' bot. Does not move or act, only grows, drops fruit, and
  * eventually dies. See [[com.alexanderstrada.replica.bot.plant.Fruit]].*/
class Plant(
    val pos: Vector2d,
    var size: Double,
    val genome: Genome,
    w: World) extends Bot {

  override val toMaxBoundingRect = Rect(pos, vectorMaxSize * w.rules.plantCanopyMultiplier)
  override def toBoundingRect = Rect(pos, vectorSize * w.rules.plantCanopyMultiplier)

  private val fruitCount = genome(Genome.FRUIT_COUNT).round.toInt

  private var germed = false
  private var fedThisTick = false
  private var fruitsSize = 0.0
  private var remainingLifespan = w.rules.calcPlantLifespan(genome)

  /** Advance one tick. Starve if not fed this tick.*/
  def tick() = {
    if (!fedThisTick) starve()
    fedThisTick = false
    age()
  }

  /** Grow if not yet germed, otherwise fruit.*/
  def feed() = {
    fedThisTick = true
    if (!germed) grow()
    else fruit()
  }

  /** Age. Die if `remainingLifespan` falls to `0`.*/
  private def age() = {
    remainingLifespan -= 1
    if (remainingLifespan <= 0)
      w.delete(this)
  }

  /** Shrink. Die if `size` falls below `1.0`.*/
  private def starve() = {
    size -= size * w.rules.plantStarvationRate
    if (size < 1.0) w.delete(this)
  }

  /** Grow. Germ at target size.*/
  private def grow() = {
    size += w.rules.plantGermFeedValue
    if (size >= genome(Genome.SIZE_TO_FRUIT))
      germed = true
  }

  /** Grow fruit by `world.rules.plantFruitFeedValue` and spawn if fully grown.*/
  private def fruit() = {
    val fruitGrowth = w.rules.plantFruitFeedValue / fruitCount
    fruitsSize += fruitGrowth
    if (fruitsSize >= genome(Genome.FRUIT_SIZE))
      spawnFruit()
  }

  /** Spawn fruit, reset for next crop.*/
  private def spawnFruit() =  {
    def mk() = new Fruit(
                 w.rules.calcFruitDropPos(this, fruitsSize),
                 fruitsSize,
                 genome.reproduceAsexually(w.rules.mutationChance))

    val fruits = (0 until fruitCount).map(_ => mk()).toSet
    w.insertAll(fruits)
    fruitsSize = 0.0
  }

  /** Get the size of this plant as a Vector2d.*/
  private def vectorSize = Vector2d.withBothAs(size)

  /** Get the maximum size of this plant as a Vector2d.*/
  private def vectorMaxSize = Vector2d.withBothAs(genome(Genome.SIZE_TO_FRUIT))
}
