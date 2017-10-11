package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.bot.Genome
import com.alexanderstrada.replica.bot.Genome.Gene
import com.alexanderstrada.replica.bot.plant.Plant
import com.alexanderstrada.replica.io.swing.Display
import com.alexanderstrada.replica.sim.Simulator

class Data(sim: Simulator, world: World, display: Display) {
  type DataPoint = (Int, (Double, Double, Double))

  private val collectInterval = 10000
  private var _data = Map.empty[Gene, Seq[DataPoint]]

  def latest(g: Gene) = _data.get(g).map(_.head)

  def update() = if (sim.clock % collectInterval == 0) Genome.allPlantGenes.foreach(g => {
    val sorted = plantsSortedBy(_.genome(g))
    val lowAverageHigh = (sorted.head, format(sorted.sum / sorted.size), sorted.last)
    val out = (sim.clock, lowAverageHigh) +: _data.getOrElse(g, Seq.empty[DataPoint])
    _data = _data.updated(g, out)
    display.updateData(this)
  })

  private def plantsSortedBy(f: (Plant) => Double) =
    world.plants.toSeq.map(f).map(format).sorted

  private def format(i: Double) = (i * 100).round/100.0
}
