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

import org.quartz.JobBuilder._
import org.quartz.SimpleScheduleBuilder._
import org.quartz.CronScheduleBuilder._
import org.quartz.CalendarIntervalScheduleBuilder._
import org.quartz.TriggerBuilder._
import org.quartz.DateBuilder._

class SquartzCronBuilder(
  scheduleBuilder: CronScheduleBuilder,
  squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None
) extends SquartzBuilder[SquartzCronBuilder](squartzFuncOpt) {

  def this(cronStr: String) = this(cronSchedule(cronStr), None)
  def this(
    cronStr: String,
    squartzFunc: (JobExecutionContext) => Unit
  ) = this(cronSchedule(cronStr), Some(squartzFunc))

  override protected def getScheduleBuilder = scheduleBuilder
}

class SquartzSimpleBuilder(
  scheduleBuilder: SimpleScheduleBuilder,
  squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None
) extends SquartzBuilder[SquartzSimpleBuilder](squartzFuncOpt) {

  def this() = this(simpleSchedule)
  def this(squartzFunc: (JobExecutionContext) => Unit) = this(simpleSchedule, Some(squartzFunc))

  def scheduleWithIntervalInHours(intervalHours: Int): SquartzSimpleBuilder = {
    scheduleBuilder.withIntervalInHours(intervalHours)
    this
  }

  def scheduleWithIntervalInMilliseconds(intervalInMillis: Long): SquartzSimpleBuilder = {
    scheduleBuilder.withIntervalInMilliseconds(intervalInMillis)
    this
  }

  def scheduleWithIntervalInMinutes(intervalInMinutes: Int): SquartzSimpleBuilder = {
    scheduleBuilder.withIntervalInMinutes(intervalInMinutes)
    this
  }

  def scheduleWithIntervalInSeconds(intervalInSeconds: Int): SquartzSimpleBuilder = {
    scheduleBuilder.withIntervalInSeconds(intervalInSeconds)
    this
  }

  def scheduleWithRepeatCount(triggerRepeatCount: Int): SquartzSimpleBuilder = {
    scheduleBuilder.withRepeatCount(triggerRepeatCount)
    this
  }

  def scheduleRepeatForever: SquartzSimpleBuilder = {
    scheduleBuilder.repeatForever
    this
  }

  /*
  def schedule: SquartzSimpleBuilder = {
    scheduleBuilder.foreach( _.)
    this
  }
  */

  override protected def getScheduleBuilder = scheduleBuilder
}

abstract class SquartzBuilder[T](
  protected val squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None
) {

  //Allows building with JobDetail
  //If not specified will only allow trigger to be configured
  //and passed in for scheduling
  val jobBuilderOpt = squartzFuncOpt match {
    case Some(squartzFunc) =>
      val jobDataMap = new JobDataMap()
      jobDataMap.put("scalaFunc", squartzFunc)
      Some(newJob(classOf[ScalaJob]).usingJobData(jobDataMap))
    case None => None
  }

  val triggerBuilder = newTrigger

  def jobRequestRecovery: T = {
    jobBuilderOpt.foreach( _.requestRecovery )
    this.asInstanceOf[T]
  }

  def jobRequestRecovery(jobShouldRecover: Boolean): T = {
    jobBuilderOpt.foreach( _.requestRecovery(jobShouldRecover))
    this.asInstanceOf[T]
  }

  def jobStoreDurably: T = {
    jobBuilderOpt.foreach(_.storeDurably)
    this.asInstanceOf[T]
  }

  def jobStoreDurably(jobDurability: Boolean): T = {
    jobBuilderOpt.foreach(_.storeDurably(jobDurability))
    this.asInstanceOf[T]
  }

  def jobUsingJobData(newJobDataMap: JobDataMap): T = {
    jobBuilderOpt.foreach(_.usingJobData(newJobDataMap))
    this.asInstanceOf[T]
  }

  def jobWithDescription(jobDescription: String): T = {
    jobBuilderOpt.foreach(_.withDescription(jobDescription))
    this.asInstanceOf[T]
  }

  def jobWithIdentity(jobKey: JobKey): T = {
    jobBuilderOpt.foreach(_.withIdentity(jobKey))
    this.asInstanceOf[T]
  }

  def jobWithIdentity(name: String): T = {
    jobBuilderOpt.foreach(_.withIdentity(name))
    this.asInstanceOf[T]
  }

  def jobWithIdentity(name: String, group: String): T = {
    jobBuilderOpt.foreach(_.withIdentity(name, group))
    this.asInstanceOf[T]
  }
  /*
  def job: SquartzBuilder = {
    jobBuilderOpt.foreach( _.)
    this
  }
  */

  def triggerEndAt(triggerEndTime: Date): T = {
    triggerBuilder.endAt(triggerEndTime)
    this.asInstanceOf[T]
  }

  def triggerStartAt(triggerStartTime: Date): T = {
    triggerBuilder.startAt(triggerStartTime)
    this.asInstanceOf[T]
  }

  def triggerStartNow: T = {
    triggerBuilder.startNow
    this.asInstanceOf[T]
  }

  def triggerWithIdentity(name: String): T = {
    triggerBuilder.withIdentity(name)
    this.asInstanceOf[T]
  }

  def triggerWithIdentity(name: String, group: String): T = {
    triggerBuilder.withIdentity(name, group)
    this.asInstanceOf[T]
  }

  def triggerWithPriority(triggerPriority: Int): T = {
    triggerBuilder.withPriority(triggerPriority)
    this.asInstanceOf[T]
  }

  /*
  def trigger: SquartzBuilder = {
    triggerBuilder.
    this
  }
  */

  protected def getScheduleBuilder: ScheduleBuilder[_ <: Trigger]

  def start: (Date,Trigger,Option[JobDetail]) = {
    triggerBuilder.withSchedule(getScheduleBuilder)
    val trigger = triggerBuilder.build
    jobBuilderOpt match {
      case Some(jobBuilder) =>
        val jobDetail = jobBuilder.build
        (Squartz.sched(jobDetail, trigger), trigger, Some(jobDetail))
      case None =>
        (Squartz.sched(trigger), trigger, None)
    }
  }
}

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
    cronStr: String,
    func: (JobExecutionContext) => Unit
  ) = new SquartzCronBuilder(cronStr,func)

  /*def cronBuilder(
    func: (JobExecutionContext) => Unit
  ) = new SquartzCronBuilder(func)*/


  /*def schedCron(
    func: (JobExectionContext) => Unit,
    cronStr: String,
    triggerKey: Option[TriggerKey] = None,
    startDate: Option[Date] = None,
    endDate: Option[Date] = None
  ): (Date, TriggerKey) = {
    val triggerBuilder = newTrigger
      .withSchedule(cronSchedule(cronStr))

    (sched(func, trigger), trigger.getKey)
  }

  def schedSimpleForever(
    func: (JobExectionContext) => Unit,
    repeatInterval: Int,
    repeatUnit: Time
  ): (Date, TriggerKey) = {
    schedSimple(func, repeatInterval, repeatUnit, -1)
  }

  def schedSimple(
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
