package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.sim.SimClock
import com.alexanderstrada.replica.space2d.{Calc, Rect, Vector2d}

import scala.util.Random

/**
  * Distributes 'environmental energy' among plants spatially, by dividing the map into a grid of cells. Each cell can
  * feed one plant per tick. Plants may occupy multiple cells, and will 'block' all of them. Internally, each cell is an
  * integer equal to the last `Simulator.clock` time in which the given cell was 'fed from'. When a cell is fed from,
  * its value is updated with the current clock time.<br>
  * <br>
  * This method proved substantially faster and more effective than several other attempts at forcing plants to spread
  * out. By using the clock time, we avoid having to iterate the array at the end of each tick to 'reset' each cell. An
  * added benefit of this method is that making any given cell 'barren', or unable to support life ever, is as easy as
  * setting that cell's integer value to `Int.MaxValue`, the 'end of time', so to speak.
  */
class ArableMap(
    worldWidth: Double,
    worldHeight: Double,
    cellsPerAxis: Int,
    barrenRate: Double,
    simClock: SimClock) {

  val cellWidth = worldWidth/cellsPerAxis
  val cellHeight = worldHeight/cellsPerAxis

  private val _grid = Array.tabulate(Calc.square(cellsPerAxis).toInt)(_ => maybeBarren)

  /** Return true if the given world-space vector is contained by a cell that has fed a plant this turn.*/
  def isVectorTapped(v: Vector2d) = get(toGridX(v.x), toGridY(v.y)) >= simClock.clock

  /** Converts the given world-space rectangle to grid-space, taps each
    * untapped cell it touches, and returns the number of cells tapped.*/
  def imprint(r: Rect) = {
    val (minX, maxX) = (toGridX(r.left), toGridX(r.right))
    val (minY, maxY) = (toGridY(r.top), toGridY(r.bottom))
    var count = 0

    for (x <- minX to maxX; y <- minY to maxY) {
      val pass = get(x, y) < simClock.clock
      if (pass) {
        set(x, y, simClock.clock)
        count += 1
      }
    }

    count
  }

  /** Run `f` once for each arable cell in this map, where `f` is a procedure
    * taking the `x` and `y` values for a given arable cell in the grid.*/
  def forEachArableCell(f: (Int, Int) => Unit) = {
    for (i <- _grid.indices)
      if (_grid(i) != Int.MaxValue)
        f(i%cellsPerAxis, i/cellsPerAxis)
  }

  /** Produces a starting cell value that has a chance (defined by `Rules.set.arableCellBarrenRate`) to be barren.*/
  private def maybeBarren = if (Random.nextDouble < barrenRate) Int.MaxValue else -1

  /** Return the value of the given grid cell.*/
  private def get(x: Int, y: Int) = _grid(y*cellsPerAxis+x)

  /** Set the value of the given grid cell.*/
  private def set(x: Int, y: Int, int: Int) = _grid.update(y*cellsPerAxis+x, int)

  /** Convert a world-space X coordinate to grid-space.*/
  private def toGridX(d: Double) = (d / cellWidth).toInt

  /** Convert a world-space Y coordinate to grid-space.*/
  private def toGridY(d: Double) = (d / cellHeight).toInt
}
