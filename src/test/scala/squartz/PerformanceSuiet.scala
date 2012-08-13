package squartz

import java.util.Date

import scala.util.Random

import org.scalatest.FunSuite

class PerformanceSuite extends FunSuite {

  val rand = new Random

  val hundredRandSeconds = for( i <- 1 to 100 ) yield rand.nextInt(60)

  test("Fire one event 5 seconds in the future") {
    Squartz.startup

    var i = 0
    val startTime = System.currentTimeMillis
    hundredRandSeconds.foreach(randSecond => {
      i += 1
      val j = i
      val curTime = System.currentTimeMillis
      Squartz.schedSimpleOnce( _ => {
        val startTime = System.currentTimeMillis
        val diffTime = startTime - curTime
        info("j: %d Seconds: %d Difftime: %d".format( j, randSecond, diffTime))
      }, new Date(curTime + (randSecond * 1000)))
    })

    info("Done scheduling: %d ms".format(System.currentTimeMillis - startTime))

    Thread.sleep(61000)

    Squartz.shutdown
  }
}
