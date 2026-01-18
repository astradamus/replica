package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.bot.Genome
import com.alexanderstrada.replica.bot.plant.Plant
import com.alexanderstrada.replica.space2d.{Rect, Vector2d}

trait Rules {
  def worldBounds: Rect
  def arableCellsPerAxis: Int
  def arableCellBarrenRate: Double

  def plantCanopyMultiplier: Double
  def plantStarvationRate: Double
  def plantGermFeedValue: Double
  def plantFruitFeedValue: Double

  def mutationChance: Double
  def basePlantGenome: Genome

  def mkStarterPlants(w: World): Set[Plant]

  def calcPlantLifespan(g: Genome): Int
  def calcDecayedFruitSize(sizeBeforeDecay: Double): Double
  def calcFruitDropPos(parent: Plant, fruitSize: Double): Vector2d
}

object Rules {

  class Standard extends Rules {

    override val worldBounds: Rect = Rect.mkFromTopLeft(0, 0, 256000, 256000)
    override val arableCellsPerAxis = 50
    override val arableCellBarrenRate = 0.5

    override val plantCanopyMultiplier = 8
    override val plantStarvationRate = 0.1
    override val plantGermFeedValue = 0.05
    override val plantFruitFeedValue = 0.05

    override val mutationChance = 0.005
    override val basePlantGenome: Genome = Genome.defaultPlant

    override def mkStarterPlants(w: World): Set[Plant] = {
      val pos = worldBounds.scale(0.75).random
      if (w.arableMap.isVectorTapped(pos))
        mkStarterPlants(w)
      else {
        val size = basePlantGenome(Genome.SIZE_TO_FRUIT)
        val plant = new Plant(pos, size, basePlantGenome, w)
        Set(plant)
      }
    }

    override def calcPlantLifespan(g: Genome): Int = (g(Genome.SIZE_TO_FRUIT) * 100).toInt
    override def calcDecayedFruitSize(sizeBeforeDecay: Double): Double = sizeBeforeDecay - 0.1
    override def calcFruitDropPos(parent: Plant, fruitSize: Double): Vector2d =
      parent.pos.offsetRandomly(parent.size * plantCanopyMultiplier*10)
  }
}
