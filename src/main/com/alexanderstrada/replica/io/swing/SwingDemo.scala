package com.alexanderstrada.replica.io.swing

import com.alexanderstrada.replica.sim.Simulator
import com.alexanderstrada.replica.world.{Data, Rules, World}

class SwingDemo {
  private val sim = new Simulator
  private val world = new World(new Rules.Standard, sim)
  private val display = new Display(sim, world)
  private val data = new Data(sim, world, display)

  // Make starter plant(s).
  world.insertAll(world.rules.mkStarterPlants(world))

  // Hook up and start the sim loop.
  sim.append(world.update)
  sim.append(data.update)
  sim.start()

  // Start the rendering loop.
  display.start()
}

object SwingDemo extends App {
  private var instance = new SwingDemo

  def restart() = {
    instance.sim.terminate()
    instance.display.terminate()
    instance = new SwingDemo
  }
}
