/**
   Copyright 2012 Edwin Fuquen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
**/
package org.squartz

import java.util.Date

import java.util.concurrent.locks.ReentrantLock

import com.codahale.jerkson.Json

import org.quartz._
import org.quartz.impl._

object Squartz {
  
  case class JdbcConfig(
      val name: String,
      val driver: String,
      val url: String,
      val user: String,
      val password: String
  )
 
  //can only call once for default scheduler
  def build : Squartz = {
    val scheduler = StdSchedulerFactory.getDefaultScheduler
    new Squartz(scheduler)
  }
  
  def build(props: java.util.Properties): Squartz = {
    val schedFact = new StdSchedulerFactory(props)
    val scheduler = schedFact.getScheduler
    new Squartz(scheduler)
  }
  
  def build(
    name: String,
    threadCount: Int = 10,
    jobStore: String = "org.quartz.simpl.RAMJobStore",
    jdbcConfigOpt: Option[JdbcConfig] = None
  ): Squartz = {
    val props = new java.util.Properties
    props.setProperty("org.quartz.scheduler.instanceName", name)
    props.setProperty("org.quartz.threadPool.threadCount", threadCount.toString)
    props.setProperty("org.quartz.jobStore.class", jobStore)
    if(jobStore == "org.quartz.impl.jdbcjobstore.JobStoreTX") {
      jdbcConfigOpt match {
        case Some(jdbcConfig) =>
          props.setProperty("org.quartz.jobStore.dataSource", jdbcConfig.name)
          props.setProperty("org.quartz.dataSource.%s.driver".format(jdbcConfig.name), jdbcConfig.driver)
          jdbcConfig.driver match {
            case "org.postgresql.Driver" =>
              props.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate")
            case _ => Unit 
          }
          props.setProperty("org.quartz.dataSource.%s.URL".format(jdbcConfig.name), jdbcConfig.url)
          props.setProperty("org.quartz.dataSource.%s.user".format(jdbcConfig.name), jdbcConfig.user)
          props.setProperty("org.quartz.dataSource.%s.password".format(jdbcConfig.name), jdbcConfig.password)
        case None =>
          throw new Exception("Need to specify jdbcConfig for " + jobStore)
      }
    }
    build(props)
  }
    
  def simpleBuilder[A <: Job](implicit squartz: Squartz, mA: Manifest[A]) = SquartzSimpleBuilder.build[A]

  def cronBuilder[A <: Job](
    cronStr: String
  )(implicit squartz: Squartz, mA: Manifest[A]) = SquartzCronBuilder.build[A](cronStr)

  def schedCron[A <: Job](
    cronStr: String,
    
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]): (Date, (String,String), (String,String)) = {

    val builder = cronBuilder[A](cronStr)
    
    configureBuilder( 
      builder,
      startDateOpt, endDateOpt,
      jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )
    runBuilder(builder)
  }

  def schedSimpleForeverExclusive[A <: Job](
    repeatInterval: Int,
    repeatUnit: Time,

    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]): (Date, (String, String), (String, String)) = {
    schedSimple[A](repeatInterval, repeatUnit, -1,
      startDateOpt, endDateOpt, jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )
  }

  def schedSimpleForeverJson[A <: Job](
    repeatInterval: Int,
    repeatUnit: Time,
    jsonStr: String,

    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]): (Date, (String, String), (String, String)) = {
    schedSimpleForever(
      repeatInterval, repeatUnit,
      startDateOpt, endDateOpt, jobIdentOpt, triggerIdentOpt,
      Some(Json.parse[Map[String,Any]](jsonStr)), triggerDataMapOpt
    )
  }

  def schedSimpleForever[A <: Job](
    repeatInterval: Int,
    repeatUnit: Time,

    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]): (Date, (String, String), (String, String)) = {
    schedSimple(repeatInterval, repeatUnit, -1,
      startDateOpt, endDateOpt, jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )
  }

  def schedSimpleOnce[A <: Job](
    date: Date,

    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]) = {
    schedSimple[A](
      0,
      SECONDS,
      1,
      Some(date),
      None,
      jobIdentOpt,
      triggerIdentOpt,
      jobDataMapOpt,
      triggerDataMapOpt
    )
  }

  def schedSimple[A <: Job](
    repeatInterval: Int,
    repeatUnit: Time,
    repeatCount: Int,
    
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  )(implicit squartz: Squartz, mA: Manifest[A]): (Date, (String, String), (String, String)) = {
    val builder = repeatUnit match {
      case SECONDS =>
        if(repeatCount >= 0) {
          SquartzSimpleBuilder.repeatSecondlyForTotalCount[A](repeatCount, repeatInterval)
        } else {
          SquartzSimpleBuilder.repeatSecondlyForever[A](repeatInterval)
        }
      case MINUTES =>
        if(repeatCount >= 0) {
          SquartzSimpleBuilder.repeatMinutelyForTotalCount[A](repeatCount, repeatInterval)
        } else {
          SquartzSimpleBuilder.repeatMinutelyForever[A](repeatInterval)
        }
      case HOURS =>
        if(repeatCount >= 0) {
          SquartzSimpleBuilder.repeatHourlyForTotalCount[A](repeatCount, repeatInterval)
        } else {
          SquartzSimpleBuilder.repeatHourlyForever[A](repeatInterval)
        }
    }

    configureBuilder( 
      builder,
      startDateOpt, endDateOpt,
      jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )
    runBuilder(builder)
  }
  
  private def runBuilder(builder: SquartzBuilder[_,_]): (Date, (String, String), (String, String)) = {
    val (schedDate, trigger, jobDetailOpt) = builder.sched
    val triggerKey = trigger.getKey
    val jobKey = trigger.getJobKey

    (schedDate, (triggerKey.getName, triggerKey.getGroup), (jobKey.getName, jobKey.getGroup))
  }

  private def configureBuilder(
    builder: SquartzBuilder[_,_],
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  ) {
    import scala.collection.JavaConversions._

    jobIdentOpt.foreach(jobIdent => {
      val (name, groupOpt) = jobIdent
      groupOpt match {
        case Some(group) => 
          builder.jobWithIdentity(name, group)
        case None =>
          builder.jobWithIdentity(name)
      }
    })

    triggerIdentOpt.foreach(triggerIdent => {
      val (name, groupOpt) = triggerIdent
      groupOpt match {
        case Some(group) => 
          builder.triggerWithIdentity(name, group)
        case None =>
          builder.triggerWithIdentity(name)
      }
    })

    startDateOpt.foreach(startDate => builder.triggerStartAt(startDate))
    endDateOpt.foreach(endDate => builder.triggerEndAt(endDate))

    jobDataMapOpt.foreach(dataMap => builder.jobUsingJobData(new JobDataMap(dataMap)))
    triggerDataMapOpt.foreach(dataMap => builder.triggerUsingJobData(new JobDataMap(dataMap)))
  }
}

class Squartz(
  scheduler: Scheduler
){

  def start = { scheduler.start; this; }

  def sched(
    trigger: Trigger
  ): Date = {
    scheduler.scheduleJob(trigger)
  }

  def sched(
    jobDetail: JobDetail,
    trigger: Trigger
  ): Date = {
    scheduler.scheduleJob(jobDetail, trigger)
  }
  
  def getJobDetail(jobName: String, jobGroup: String): Option[JobDetail] = {
    val jobDetail = scheduler.getJobDetail(new JobKey(jobName, jobGroup))
    if(jobDetail != null) {
      Some(jobDetail) 
    } else {
      None 
    }
  }

  def deleteJob(jobName: String, jobGroup: String): Boolean =
    scheduler.deleteJob(new JobKey(jobName, jobGroup))

  def shutdown { scheduler.shutdown; this; }
}

case class Time

case object SECONDS extends Time
case object MINUTES extends Time
case object HOURS extends Time

class NoJob extends Job {
   override def execute(context: JobExecutionContext){ }
}
