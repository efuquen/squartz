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
package squartz

import java.util.Date

import org.quartz._
import org.quartz.impl._

object Squartz {

  private var scheduler: Scheduler = null 

  def startup(scheculer: Scheduler) {
    if(scheduler != null) {
      this.scheduler.shutdown
      this.scheduler = null
    }
    this.scheduler = scheduler
    this.scheduler.setJobFactory(new ScalaJobFactory)
    this.scheduler.start
  }

  def startup { synchronized {
    if(scheduler != null) {
      scheduler.shutdown
      scheduler = null
    }

    scheduler = StdSchedulerFactory.getDefaultScheduler
    scheduler.setJobFactory(new ScalaJobFactory)
    scheduler.start
  }}

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

  def simpleBuilder(
    func: (JobExecutionContext) => Unit
  ) = new SquartzSimpleBuilder(func)

  def cronBuilder(
    func: (JobExecutionContext) => Unit,
    cronStr: String
  ) = new SquartzCronBuilder(cronStr,func)

  def schedCron(
    func: (JobExecutionContext) => Unit,
    cronStr: String,
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None
  ): (Date, (String,String), (String,String)) = {
    val builder = cronBuilder(func, cronStr)

    configureBuilder( 
      builder,
      startDateOpt, endDateOpt,
      jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )

    val (schedDate, trigger, jobDetailOpt) = builder.sched
    val triggerKey = trigger.getKey
    val jobKey = trigger.getJobKey

    (schedDate, (triggerKey.getName, triggerKey.getGroup), (jobKey.getName, jobKey.getGroup))
  }

  private def configureBuilder(
    builder: SquartzBuilder[_],
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

  /*def schedSimpleForever(
    func: (JobExectionContext) => Unit,
    repeatInterval: Int,
    repeatUnit: Time
  ): (Date, (String, String), (String, String)) = {
    schedSimple(func, repeatInterval, repeatUnit, -1)
  }*/

  /*def schedSimple(
    func: (JobExectionContext) => Unit,
    repeatInterval: Int,
    repeatUnit: Time,
    repeatCount: Int
  ): (Date, TriggerKey) = {
    val triggerSched = repeatUnit match {
      case SECOND =>
        simpleSchedule.withIntervalInSeconds(repeatInterval)
      case MINUTE =>
        simpleSchedule.withIntervalInMinutes(repeatInterval)
      case HOUR =>
        simpleSchedule.withIntervalInHours(repeatInterval)
    }

    if(repeatCount >= 0) {
      triggerSched.withRepeatCount(repeatCount)
    } else {
      triggerSched.repeatForever
    }

    val trigger = newTrigger
      .withSchedule(triggerSched)
      .build

    (sched(func, trigger), trigger.getKey)
  }*/

  def shutdown { synchronized {
    scheduler.shutdown
    scheduler = null
  }}
}

class ScalaJobFactory extends org.quartz.simpl.PropertySettingJobFactory {
  override def newJob(bundle: org.quartz.spi.TriggerFiredBundle, scheduler: Scheduler): Job = {
    if(bundle.getJobDetail.getJobClass.equals(classOf[ScalaJob])) {
      val func = bundle.getJobDetail.getJobDataMap.get("scalaFunc").asInstanceOf[Function1[JobExecutionContext,Unit]]
      new ScalaJob(func)
    } else {
      super.newJob(bundle, scheduler)
    }
  }
}

case class ScalaJob(
  func: (JobExecutionContext) => Unit
) extends Job {
  override def execute(context: JobExecutionContext) {
    func(context)
  }
}
