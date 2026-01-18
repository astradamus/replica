package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.sim.SimClock
import com.alexanderstrada.replica.space2d.{Calc, Rect, Vector2d}
import com.alexanderstrada.replica.world.ArableMap.ImprintOutOfBoundsException

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

  val cellWidth: Double = worldWidth/cellsPerAxis
  val cellHeight: Double = worldHeight/cellsPerAxis

  private val _grid = Array.tabulate(Calc.square(cellsPerAxis).toInt)(_ => maybeBarren)

  /** Return true if the given world-space vector is contained by a cell that has fed a plant this turn.*/
  def isVectorTapped(v: Vector2d): Boolean = {
    val gX = toGrid(v.x, cellWidth)
    val gY = toGrid(v.y, cellHeight)
    get(gX, gY) >= simClock.clock
  }

  /** Converts the given world-space rectangle to grid-space, taps each
    * untapped cell it touches, and returns the number of cells tapped.*/
  def imprint(r: Rect): Int = {

    val minX = toGrid(r.left, cellWidth)
    val minY = toGrid(r.top, cellHeight)
    val maxX = toGridExclusive(r.right, cellWidth)
    val maxY = toGridExclusive(r.bottom, cellHeight)

    if (minX < 0 || minY < 0 || maxX >= cellsPerAxis || maxY >= cellsPerAxis)
      throw new ImprintOutOfBoundsException("Tried to imprint out-of-bounds rect " + r)

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

  /** Run `p` once for each arable cell in this map, where `p` is a procedure
    * taking the `x` and `y` values for a given arable cell in the grid.*/
  def forEachArableCell(p: (Int, Int) => Unit): Unit = {
    for (i <- _grid.indices)
      if (_grid(i) != Int.MaxValue)
        p(i%cellsPerAxis, i/cellsPerAxis)
  }

  /** Produces a starting cell value that has a chance (defined by `Rules.set.arableCellBarrenRate`) to be barren.*/
  private def maybeBarren = if (Random.nextDouble < barrenRate) Int.MaxValue else -1

  /** Return the value of the given grid cell.*/
  private def get(x: Int, y: Int) = _grid(y*cellsPerAxis+x)

  /** Set the value of the given grid cell.*/
  private def set(x: Int, y: Int, int: Int): Unit = _grid.update(y*cellsPerAxis+x, int)

  /** Convert a world-space X or Y coordinate to grid-space, favoring the
    * right-/bottom-most cell when the coordinate straddles a cell border.*/
  private def toGrid(leftOrTop: Double, cellWidthOrHeight: Double) = (leftOrTop / cellWidthOrHeight).floor.toInt

  /** Convert a world-space X or Y coordinate to grid-space, favoring the
    * left-/top-most cell when the coordinate straddles a cell border.*/
  private def toGridExclusive(rightOrBottom: Double, cellWidthOrHeight: Double) = {
    val cell = (rightOrBottom / cellWidthOrHeight).toInt
    val cellStart = cell * cellWidthOrHeight
    if (rightOrBottom - cellStart > 0.0) cell else cell - 1
  }
}

object ArableMap {
  class ImprintOutOfBoundsException(message: String) extends IndexOutOfBoundsException(message)
}