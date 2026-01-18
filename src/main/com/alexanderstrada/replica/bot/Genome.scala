package com.alexanderstrada.replica.bot

import java.awt.Color

import com.alexanderstrada.replica.bot.Genome.Gene
import com.alexanderstrada.replica.space2d.Calc

import scala.util.Random

/** Defines the characteristics that a bot can pass on to its offspring.*/
case class Genome(genes: Map[Gene, Double]) {
  def apply(gene: Gene): Double = genes(gene)

  def color = new Color((genes(Genome.COLOR_RED) * 255).round.toInt,
                        (genes(Genome.COLOR_GRN) * 255).round.toInt,
                        (genes(Genome.COLOR_BLU) * 255).round.toInt)

  def reproduceAsexually(mutationChance: Double): Genome = copy(genes.map(kv => {
    val (gene, value) = kv

    val changeRange = value * gene.selectVariance(mutationChance)
    val changeSign = if (Random.nextBoolean) -1 else 1
    val change = Random.nextDouble() * changeRange * changeSign

    (gene, Calc.clamp(value + change, gene.minimum, gene.maximum))
  }))
}

object Genome {

  sealed class Gene(val base: Double,
                    val minimum: Double = Double.MinPositiveValue,
                    val maximum: Double = Double.MaxValue,
                    val stdVariance: Double = 0.01,
                    val mutVariance: Double = 0.25) {
    def selectVariance(mutationChance: Double): Double = if (Random.nextDouble() < mutationChance) mutVariance else stdVariance
  }

  case object COLOR_RED                   extends Gene(0.30, 0.25, 1.0)
  case object COLOR_GRN                   extends Gene(0.30, 0.25, 1.0)
  case object COLOR_BLU                   extends Gene(0.30, 0.25, 1.0)

  case object SIZE_TO_FRUIT               extends Gene(100.0, 10.0)
  case object FRUIT_SIZE                  extends Gene(5.0)
  case object FRUIT_COUNT                 extends Gene(2.0)

  def allPlantGenes: Seq[Gene] = Seq(COLOR_RED,
                                     COLOR_GRN,
                                     COLOR_BLU,
                                     SIZE_TO_FRUIT,
                                     FRUIT_SIZE,
                                     FRUIT_COUNT)

  def defaultPlant = Genome(allPlantGenes.map(g => (g, g.base)).toMap)
}
