package squartz

import java.util.Date

import Squartz._

import org.scalatest.FunSuite

class FireOnceSuite extends FunSuite {

  test("Fire one event 5 seconds in the future") {
    implicit val squartz = Squartz.build.start

    val curTime = System.currentTimeMillis
    var diffTime = 0L
    schedSimpleOnce( _ => {
      val startTime = System.currentTimeMillis
      diffTime = startTime - curTime
    }, new Date(curTime + 5000))

    Thread.sleep(6000)

    info("DiffTime: " + diffTime)
    assert(4900 < diffTime)
    assert(diffTime < 5100)
  }
}
