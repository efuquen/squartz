package squartz

import Squartz._

object Main extends App {

  val jobLock = new java.util.concurrent.locks.ReentrantLock 

  implicit val squartz = Squartz.build.start
  
  val (schedDate, triggerTup, jobTup) = schedSimpleForeverExclusive(
   _ => { 
     println("Hello, World!!!")
     Thread.sleep(5000)
   },
   (lockedTime: Long) => {
     val timeLocked = System.currentTimeMillis - lockedTime
     println("Locked " + timeLocked)
   },
   3,
   SECONDS
  )

  println(schedDate + " " + triggerTup + " " + jobTup)

  Thread.sleep(16000)
  squartz.shutdown
}
