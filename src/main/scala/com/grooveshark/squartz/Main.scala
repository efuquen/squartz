package com.grooveshark.squartz

object Main extends App {

  Squartz.startup
  
  val squartzBuilder = Squartz.simpleBuilder(
    _ => println("Hello, World!!!")
  )

  squartzBuilder
    .scheduleWithIntervalInSeconds(3) 
    .scheduleRepeatForever 
    .start 

  Thread.sleep(16000)
  Squartz.shutdown
}
