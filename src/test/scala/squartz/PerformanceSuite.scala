package org.squartz 

import java.util.Date

import scala.util.Random
import Squartz._
import org.scalatest.fixture
import org.quartz.JobExecutionContext
import org.quartz.Job
import PerformanceSuite._

class PerformanceSuite extends fixture.FunSuite {
  
  type FixtureParam = (Squartz, Squartz)

  val rand = new Random

  val perfRunTimeMs = 10000
  
  val fiftyRandMs = for( i <- 1 to 50 ) yield rand.nextInt(perfRunTimeMs).toLong
  val hundredRandMs = for( i <- 1 to 100 ) yield rand.nextInt(perfRunTimeMs).toLong
  val twohundredRandMs = for( i <- 1 to 200 ) yield rand.nextInt(perfRunTimeMs).toLong
  val threehundredRandMs = for( i <- 1 to 300 ) yield rand.nextInt(perfRunTimeMs).toLong
  
  val thousandRandMs = for( i <- 1 to 1000 ) yield rand.nextInt(perfRunTimeMs).toLong
  val fiveThousandRandMs= for( i <- 1 to 5000 ) yield rand.nextInt(perfRunTimeMs).toLong
  
  def withFixture(test: OneArgTest){
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
    ))).start
    try {
      test((ramSquartz, jdbcSquartz)) 
    } finally {
      ramSquartz.shutdown
      jdbcSquartz.shutdown
    }
  }

  /*test("50 scheduled tasks in 10 seconds ") { squartzs: (Squartz, Squartz) =>
    val (ramSquartz: Squartz, jdbcSquartz: Squartz) = squartzs
    val offsetStartTime = 1000
    perfTest(fiftyRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "ram")(ramSquartz)
    perfTest(fiftyRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "jdbc")(jdbcSquartz) 
  }

  test("100 scheduled tasks in 10 seconds ") { squartzs: (Squartz, Squartz) =>
    val (ramSquartz: Squartz, jdbcSquartz: Squartz) = squartzs
    val offsetStartTime = 2000
    perfTest(hundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "ram")(ramSquartz)
    perfTest(hundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "jdbc")(jdbcSquartz) 
  }
  
  test("200 scheduled tasks in 10 seconds ") { squartzs: (Squartz, Squartz) =>
    val (ramSquartz: Squartz, jdbcSquartz: Squartz) = squartzs
    val offsetStartTime = 5000
    perfTest(twohundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "ram")(ramSquartz)
    perfTest(twohundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "jdbc")(jdbcSquartz) 
  }
  
  test("300 scheduled tasks in 10 seconds ") { squartzs: (Squartz, Squartz) =>
    val (ramSquartz: Squartz, jdbcSquartz: Squartz) = squartzs
    val offsetStartTime = 10000
    perfTest(threehundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "ram")(ramSquartz)
    perfTest(threehundredRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "jdbc")(jdbcSquartz) 
  }

  test("1000 scheduled tasks in 10 seconds ") { squartzs: (Squartz, Squartz) =>
    val (ramSquartz: Squartz, jdbcSquartz: Squartz) = squartzs
    val offsetStartTime = 30000
    //perfTest(thousandRandMs, perfRunTimeMs + offsetStartTime + 1000, offsetStartTime, 50, "ram")(ramSquartz)
    perfTest(thousandRandMs, perfRunTimeMs + offsetStartTime * 2, offsetStartTime, 50, "jdbc")(jdbcSquartz)
  }*/

  /*test("5000 scheduled tasks in 10 seconds") {
    perfTest(fiveThousandRandMs, perfRunTimeMs + 1000, 50)(ramSquartz)
    perfTest(fiveThousandRandMs, perfRunTimeMs + 1000, 50)(jdbcSquartz)
  }*/
  
  def perfTest(randMilliseconds: Seq[Long], sleepTime: Long, offsetStartTime: Long, accuracy: Long, name: String)(implicit squartz: Squartz) {
    val startTime = System.currentTimeMillis
    randMilliseconds.foreach(randMillisecond => {
      schedSimpleOnce[PerfTestJob](
        new Date(startTime + offsetStartTime + randMillisecond),
        jobDataMapOpt = Some(Map(
          "startTime" -> (startTime + offsetStartTime),
          "randMillisecond" -> randMillisecond
        ))
    )
    })
    
    info("%s - Done scheduling %d jobs in %d ms".format(name, randMilliseconds.size, System.currentTimeMillis - startTime))
    Thread.sleep(sleepTime)
    
    //diffTimes.foreach(diffTime => assert(diffTime < accuracy))
    val avg = diffTimes.reduceLeft(_ + _)/diffTimes.size
    val variance = diffTimes.map(x => math.pow(x - avg, 2.0)).reduceLeft(_ + _) / diffTimes.size
    val stddev = math.sqrt(variance)
    info("%s - NumOfJobsProcessed: %d MaximumDelay: %f ms AvgerageDelay: %f ms StandardDeviationDelay: %f ms".format(
        name, diffTimes.size, diffTimes.max, avg, stddev))
    
    diffTimes = List[Double]()
  }
}

class PerfTestJob extends Job {
  override def execute(context: JobExecutionContext) {
    val jobDataMap = context.getJobDetail().getJobDataMap() 
    val startTime = jobDataMap.getLongValue("startTime")
    val randMillisecond = jobDataMap.getLongValue("randMillisecond")
    
    val runTime = System.currentTimeMillis - startTime
    val diffTime = (runTime - randMillisecond).abs
    
    diffTimes = diffTime :: diffTimes
  }
}

object PerformanceSuite {
  var diffTimes = List[Double]()
}
