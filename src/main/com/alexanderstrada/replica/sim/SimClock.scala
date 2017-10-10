package com.alexanderstrada.replica.sim

trait SimClock {

  /** Return the 'clock time' for the simulation, which is the number
    * of ticks that have been processed since the simulation began.*/
  def clock: Int
}
