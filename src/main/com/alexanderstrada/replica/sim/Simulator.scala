package com.alexanderstrada.replica.sim

import scala.util.Random

class Simulator extends SimClock {

  @volatile var simPaused = true
  @volatile var simDelay = 0.0
  @volatile private var _clock = 0
  @volatile private var _ticksPerSecond = 0.0

  private var running = true
  private val thread = new Thread(() => while (running) loop())

  private var _routines = Seq.empty[() => Unit]

  /** The number of ticks that have elapsed since the simulation began.*/
  def clock: Int = _clock

  /** The most recently calculated ticking speed.*/
  def ticksPerSecond: Double = _ticksPerSecond

  /** Add a function to the end of the list of routines performed each tick by this Simulator.*/
  def append(f: () => Unit): Unit = {
    if (thread.isAlive)
      throw new RuntimeException("Cannot append to a simulator after it has been started.")

    _routines :+= f
  }

  /** Begins the simulation.*/
  def start(): Unit = thread.start()

  /** Terminate the simulation.*/
  def terminate(): Unit = { running = false }

  /** Run through one tick of the simulator.*/
  private def loop(): Unit = if (!simPaused) {
    val nt = System.nanoTime()
    _routines.foreach(_())
    _clock += 1
    _ticksPerSecond = 1.0 / ((System.nanoTime() - nt) / 1000000000.0)
    delay(simDelay)
  }

  /** Potentially cause the simulation thread to wait, depending on input.
    * <br>- If input is `>=` 1.0, it is rounded and the thread waits that many ms.
    * <br>- If input is `>` 0.0 but `<` 1.0, it is a percentage chance the thread will wait for 1ms.
    * <br>- If input is `<=` 0.0, the thread will not wait.*/
  private def delay(d: Double): Unit = {
    lazy val l = if (d >= 1.0) d.round else if (Random.nextDouble < d) 1L else 0L

    if (d > 0.0 && l >= 1) this.synchronized {
      wait(l)
    }
  }
}
