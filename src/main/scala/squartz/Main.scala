package squartz

object Main extends App {

  val jobLock = new java.util.concurrent.locks.ReentrantLock 

  Squartz.startup
  
  val (schedDate, triggerTup, jobTup) = Squartz.schedSimpleForeverExclusive(
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
  Squartz.shutdown
}
