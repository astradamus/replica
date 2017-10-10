package com.alexanderstrada.replica.world

import com.alexanderstrada.replica.sim.SimClock
import com.alexanderstrada.replica.space2d.{Calc, Rect}
import org.scalatest._

class ArableMapTest extends FlatSpec with Matchers {

  private val mapSize = 100
  private val cellsPerAxis = 10

  private def mkMap = new ArableMap(mapSize, mapSize, cellsPerAxis, 0.0, new SimClock { val clock = 0 })

  it should "return cellsPerAxis^2 when fully imprinted within bounds" in {
    mkMap.imprint(Rect.mkFromTopLeft(0, 0, mapSize, mapSize)) should be (Calc.square(cellsPerAxis))
  }

  it should "reject imprints that are out of bounds" in {
    val m = mkMap
    m.imprint(Rect.mkFromTopLeft(0, 0, mapSize, mapSize)) should be (Calc.square(cellsPerAxis))
    an [IndexOutOfBoundsException] shouldBe thrownBy { m.imprint(Rect.mkFromTopLeft(-1, 0, mapSize, mapSize)) }
    an [IndexOutOfBoundsException] shouldBe thrownBy { m.imprint(Rect.mkFromTopLeft(0, -1, mapSize, mapSize)) }
    an [IndexOutOfBoundsException] shouldBe thrownBy { m.imprint(Rect.mkFromTopLeft(1, 0, mapSize, mapSize)) }
    an [IndexOutOfBoundsException] shouldBe thrownBy { m.imprint(Rect.mkFromTopLeft(0, 1, mapSize, mapSize)) }
  }

  it should "not be crashed by imprints that sit on its upper bounds" in {
    mkMap.imprint(new Rect(90, 90, 20, 20)) should be (4)
  }
}
