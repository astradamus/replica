package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.bot.Bot
import com.alexanderstrada.replica.bot.plant.{Fruit, Plant}
import com.alexanderstrada.replica.sim.SimClock
import com.alexanderstrada.replica.space2d.QuadTree

class World(val rules: Rules, simClock: SimClock) {

  val bounds = rules.worldBounds
  val arableMap = new ArableMap(
    bounds.w,
    bounds.h,
    rules.arableCellsPerAxis,
    rules.arableCellBarrenRate,
    simClock)

  private var _plants = Set.empty[Plant]
  private val qt_plants = QuadTree.empty[Plant](bounds, 16, 12)
  private var plantsBySize = List.empty[Plant]

  private var _fruits = Set.empty[Fruit]
  private val qt_fruits = QuadTree.empty[Fruit](bounds, 16, 12)

  def plants = _plants
  def fruits = _fruits

  /** Advance the world by one tick.*/
  def update() = {
    feedPlants()
    tickPlants()
    tickFruits()
  }

  /** Insert the given bot into the World.*/
  def insert[A <: Bot](a: A): Unit = if (bounds.contains(a.toMaxBoundingRect)) a match {
    case p: Plant if !_plants.contains(p) =>
      _plants += p
      plantsBySize :+= p
      qt_plants.insert(p)
    case f: Fruit if !_fruits.contains(f) =>
      _fruits += f
      qt_fruits.insert(f)
    case _ =>
  }

  /** Remove the given bot from the World.*/
  def delete[A <: Bot](a: A): Unit = a match {
    case p: Plant if _plants.contains(p) =>
      plantsBySize.indexOf(p) match {
        case -1 => throw new RuntimeException("Plant in set `_plants` not found in seq 'plantsBySize'.")
        case i => plantsBySize = plantsBySize.patch(i, Nil, 1)
      }
      _plants -= p
      qt_plants.remove(p)
    case f: Fruit if _fruits.contains(f) =>
      _fruits -= f
      qt_fruits.remove(f)
    case _ =>
  }

  /** Insert all of the given bots into the World.*/
  def insertAll[A <: Bot](as: Set[A]) = as foreach insert

  /** Feed plants using an `ArableMap` to ensure only the tallest plant at any given point
    * is fed. See [[com.alexanderstrada.replica.world.ArableMap]] for more details.*/
  private def feedPlants() = plantsBySize.sortBy(- _.size).foreach(p => {
    val ate = arableMap.imprint(p.toBoundingRect)
    (0 until ate).foreach(_ => p.feed())
  })

  /** Advance all plants one tick.*/
  private def tickPlants() = _plants.foreach(_.tick())

  /** Advance all fruits one tick.*/
  private def tickFruits() = _fruits.foreach(_.tick(this))
}
