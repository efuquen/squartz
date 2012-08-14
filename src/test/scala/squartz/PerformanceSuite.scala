package squartz 

import java.util.Date

import scala.util.Random

import Squartz._

import org.scalatest.FunSuite

class PerformanceSuite extends FunSuite {

  val rand = new Random

  val perfRunTimeMs = 10000
  val hundredRandMs = for( i <- 1 to 100 ) yield rand.nextInt(perfRunTimeMs).toLong
  val thousandRandMs = for( i <- 1 to 1000 ) yield rand.nextInt(perfRunTimeMs).toLong
  val fiveThousandRandMs= for( i <- 1 to 5000 ) yield rand.nextInt(perfRunTimeMs).toLong

  val ramSquartz = Squartz.build("perf-ram", threadCount = 10).start
  val jdbcSquartz = Squartz.build(
    "perf-jdbc", threadCount = 10,
    jobStore = "org.quartz.impl.jdbcjobstore.JobStoreTX",
    jdbcConfigOpt = Some(JdbcConfig(
      "quartz-test",
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost/quartz-test",
      "edwin",
      "password"
  )))

  test("100 scheduled tasks in 10 seconds ") {
    perfTest(hundredRandMs, perfRunTimeMs  + 1000, 50)(ramSquartz)
    perfTest(hundredRandMs, perfRunTimeMs  + 1000, 50)(jdbcSquartz)
  }

  /*test("1000 scheduled tasks in 10 seconds ") {
    perfTest(thousandRandMs, perfRunTimeMs + 1000, 50)(ramSquartz)
    perfTest(thousandRandMs, perfRunTimeMs + 1000, 50)(jdbcSquartz)
  }

  test("5000 scheduled tasks in 10 seconds") {
    perfTest(fiveThousandRandMs, perfRunTimeMs + 1000, 50)(ramSquartz)
    perfTest(fiveThousandRandMs, perfRunTimeMs + 1000, 50)(jdbcSquartz)
  }*/

  def perfTest(randMilliseconds: Seq[Long], sleepTime: Long, accuracy: Long)(implicit squartz: Squartz) {
    var diffTimes = List[Double]()
    val startTime = System.currentTimeMillis + 10000
    randMilliseconds.foreach(randMillisecond => {
      schedSimpleOnce( _ => {
        val runTime = System.currentTimeMillis - startTime
        val diffTime = (runTime - randMillisecond).abs
        
        assert(diffTime < accuracy)
        diffTimes = diffTime :: diffTimes
      }, new Date(startTime + randMillisecond))
    })

    info("Done scheduling in %d ms".format(System.currentTimeMillis - startTime + 10000))
    Thread.sleep(sleepTime)
    val avg = diffTimes.reduceLeft(_ + _)/diffTimes.size
    val variance = diffTimes.map(x => math.pow(x - avg, 2.0)).reduceLeft(_ + _) / diffTimes.size
    val stddev = math.sqrt(variance)
    info("MaxDiffTime: %f AvgDiffTime: %f StdDev: %f".format(diffTimes.max, avg, stddev))
  }
}
