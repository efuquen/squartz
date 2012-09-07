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
import org.quartz.impl.matchers.GroupMatcher

import scala.collection.JavaConversions._

object Squartz {
  
  case class JdbcConfig(
      val name: String,
      val driver: String,
      val url: String,
      val user: String,
      val password: String
  )

  implicit def mapToJProps(map: Map[String,String]): java.util.Properties = {
    val javaProperties = new java.util.Properties
    for((key, value) <- map) {
      javaProperties.setProperty(key, value)
    }
    javaProperties
  }
 
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
    val props = Map[String,String](
      "org.quartz.scheduler.instanceName" -> name,
      "org.quartz.threadPool.threadCount" -> threadCount.toString,
      "org.quartz.jobStore.class" -> jobStore
    ) ++ (if(jobStore == "org.quartz.impl.jdbcjobstore.JobStoreTX") {
      jdbcConfigOpt match {
        case Some(jdbcConfig) =>
          val jdbcConfigNamespace = "org.quartz.dataSource." + jdbcConfig.name 
          Map[String,String](
            "org.quartz.jobStore.dataSource" -> jdbcConfig.name,
            jdbcConfigNamespace + ".driver" -> jdbcConfig.driver,
            jdbcConfigNamespace + ".URL" -> jdbcConfig.url,
            jdbcConfigNamespace + ".user" -> jdbcConfig.user,
            jdbcConfigNamespace + ".password" -> jdbcConfig.password
          ) ++ (if(jdbcConfig.driver == "org.postgresql.Driver") {
            Map[String,String]("org.quartz.jobStore.driverDelegateClass" -> 
                "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate")
          } else {
            Map[String,String]()
          })
        case None =>
          throw new Exception("Need to specify jdbcConfig for " + jobStore)
      }
    } else {
      Map[String,String]()
    })
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

  def getTriggersForJob(jobName: String, jobGroup: String): Seq[Trigger] =
    getTriggersForJob(new JobKey(jobName, jobGroup))

  def getTriggersForJob(jobKey: JobKey): Seq[Trigger] =
    scheduler.getTriggersOfJob(jobKey)

  def getJobGroupNames: Seq[String] = scheduler.getJobGroupNames

  def getJobKeysForGroup(groupName: String): Seq[JobKey] =
    scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).toArray(Array[JobKey]())

  def getAllJobKeys: Seq[JobKey] =
    getJobGroupNames.map(getJobKeysForGroup(_)).flatten

  def getJobsForGroup(groupName: String): Seq[JobDetail] =
    getJobKeysForGroup(groupName).map(getJob(_))

  def getAllJobs: Seq[JobDetail] = getAllJobKeys.map(getJob(_))

  def getJob(jobKey: JobKey): JobDetail =
    scheduler.getJobDetail(jobKey)

  def getJob(jobName: String, jobGroup: String): JobDetail =
    scheduler.getJobDetail(new JobKey(jobName, jobGroup))

  def deleteAllJobs: Boolean = scheduler.deleteJobs(getAllJobKeys)

  def deleteJobsForGroup(jobGroup: String): Boolean =
    scheduler.deleteJobs(getJobKeysForGroup(jobGroup))

  def deleteJob(jobName: String, jobGroup: String): Boolean =
    scheduler.deleteJob(new JobKey(jobName, jobGroup))

  def checkJobExists(jobName: String, jobGroup: String): Boolean =
    scheduler.checkExists(new JobKey(jobName, jobGroup))

  def getName = scheduler.getSchedulerName

  def shutdown { scheduler.shutdown; this; }
}

case class Time

case object SECONDS extends Time
case object MINUTES extends Time
case object HOURS extends Time

class NoJob extends Job {
   override def execute(context: JobExecutionContext){ }
}
