package com.alexanderstrada.replica.space2d

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RectTest extends AnyFlatSpec with Matchers {

  val r = Rect(813.15, 137.66, 731.31, 968.13)

  it should "contain itself" in {
    r.contains(r) should be (true)
  }

  it should "intersect itself" in {
    r.intersects(r) should be (true)
  }

  it should "not contain what it only intersects" in {
    val r1 = Rect(0, 0, 100, 100)
    val r2 = Rect(50, 50, 60, 60)

    r1.contains(r2) should be (false)
    r1.intersects(r2) should be (true)
  }
}
