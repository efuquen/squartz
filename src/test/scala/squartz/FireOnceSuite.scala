package org.squartz

import java.util.Date
import Squartz._
import org.scalatest.FunSuite
import org.quartz.Job
import org.quartz.JobExecutionContext

class FireOnceJob extends Job {
   override def execute(context: JobExecutionContext) { 
      val jobDataMap = context.getJobDetail().getJobDataMap()
      
      val curTime = jobDataMap.getLongValue("curTime")
      val startTime = System.currentTimeMillis
      
      //Test will need this value
      FireOnceSuite.diffTime = startTime - curTime
   }
 }

object FireOnceSuite {
  var diffTime = 0L
}
  
class FireOnceSuite extends FunSuite {
  
  import FireOnceSuite._
  
  /*test("Fire one event 5 seconds in the future") {
    implicit val squartz = Squartz.build.start

    val curTime = System.currentTimeMillis
    schedSimpleOnce[FireOnceJob](
      new Date(System.currentTimeMillis + 5000),
      
      jobDataMapOpt = Some(Map(
        "curTime" -> curTime
      ))
    )
    Thread.sleep(6000)
    info("DiffTime: " + diffTime)
    assert(4900 < diffTime)
    assert(diffTime < 5100)
    squartz.shutdown
  }*/
}
