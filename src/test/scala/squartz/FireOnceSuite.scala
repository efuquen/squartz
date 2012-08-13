package squartz

import java.util.Date

import org.scalatest.FunSuite

class FireOnceSuite extends FunSuite {

  test("Fire one event 5 seconds in the future") {

    Squartz.startup

    val curTime = System.currentTimeMillis
    var diffTime = 0L
    Squartz.schedSimpleOnce( _ => {
      val startTime = System.currentTimeMillis
      diffTime = startTime - curTime
    }, new Date(curTime + 5000))

    Thread.sleep(6000)

    info("DiffTime: " + diffTime)
    assert(4900 < diffTime)
    assert(diffTime < 5100) 

    Squartz.shutdown
  }
}
