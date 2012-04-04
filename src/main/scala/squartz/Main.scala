package squartz

object Main extends App {

  Squartz.startup
  
  val (schedDate, triggerTup, jobTup) = Squartz.schedSimpleForever(
    _ => println("Hello, World!!!"),
   3,
   SECONDS
  )

  println(schedDate + " " + triggerTup + " " + jobTup)

  Thread.sleep(16000)
  Squartz.shutdown
}
