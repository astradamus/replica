package com.alexanderstrada.replica.io.swing

import com.alexanderstrada.replica.sim.Simulator
import com.alexanderstrada.replica.world.{Rules, World}

object SwingDemo extends App {

  private val sim = new Simulator
  private val world = new World(new Rules.Standard, sim)
  private val display = new Display(sim, world)

  // Make starter plant(s).
  world.insertAll(world.rules.mkStarterPlants(world))

  // Hook up and start the sim loop.
  sim.append(world.update)
  sim.start()

  // Start the rendering loop.
  display.start()
}
