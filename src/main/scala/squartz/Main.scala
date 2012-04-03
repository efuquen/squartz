package squartz

object Main extends App {

  Squartz.startup
  
  val squartzBuilder = Squartz.simpleBuilder(
    _ => println("Hello, World!!!")
  )

  squartzBuilder
    .scheduleWithIntervalInSeconds(3) 
    .scheduleRepeatForever 
    .sched

  Thread.sleep(16000)
  Squartz.shutdown
}
