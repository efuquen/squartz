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

import java.util.concurrent.locks.ReentrantLock

import org.quartz._
import org.quartz.impl._

object Squartz {
  
  case class JdbcConfig(
      val  name: String,
      val driver: String,
      val url: String,
      val user: String,
      val password: String
  )
 
  //can only call once for default scheduler
  def build : Squartz = {
    val scheduler = StdSchedulerFactory.getDefaultScheduler
    scheduler.setJobFactory(new ScalaJobFactory)
    new Squartz(scheduler)
  }
  
  def build(props: java.util.Properties): Squartz = {
    val schedFact = new StdSchedulerFactory(props)
    val scheduler = schedFact.getScheduler
    scheduler.setJobFactory(new ScalaJobFactory)
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
          props.setProperty("org.quartz.dataSource.%s.URL".format(jdbcConfig.name), jdbcConfig.url)
          props.setProperty("org.quartz.dataSource.%s.user".format(jdbcConfig.name), jdbcConfig.user)
          props.setProperty("org.quartz.dataSource.%s.password".format(jdbcConfig.name), jdbcConfig.password)
        case None =>
          throw new Exception("Need to specify jdbcConfig for " + jobStore)
      }
    }
    build(props)
  }
    
  def simpleBuilder(
    func: (JobExecutionContext) => Unit
  )(implicit squartz: Squartz) = new SquartzSimpleBuilder(func)

  def simpleBuilder(
    func: (JobExecutionContext) => Unit,
    lockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = new SquartzSimpleBuilder(func, lockFunc)

  def cronBuilder(
    func: (JobExecutionContext) => Unit,
    cronStr: String
  )(implicit squartz: Squartz) = new SquartzCronBuilder(cronStr,func)

  def cronBuilder(
    func: (JobExecutionContext) => Unit,
    lockFunc: (Long) => Unit,
    cronStr: String
  )(implicit squartz: Squartz) = new SquartzCronBuilder(cronStr,func,lockFunc)

  def schedCron(
    func: (JobExecutionContext) => Unit,
    cronStr: String,
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None,
    lockFuncOpt: Option[(Long) => Unit] = None
  )(implicit squartz: Squartz): (Date, (String,String), (String,String)) = {

    val builder = lockFuncOpt match {
      case Some(lockFunc) =>
        cronBuilder(func, lockFunc, cronStr)
      case None =>
        cronBuilder(func, cronStr)
    }
    configureBuilder( 
      builder,
      startDateOpt, endDateOpt,
      jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt
    )
    runBuilder(builder)
  }

  def schedSimpleForeverExclusive(
    func: (JobExecutionContext) => Unit,
    lockFunc: (Long) => Unit,
    repeatInterval: Int,
    repeatUnit: Time,

    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None,
    lockFuncOpt: Option[(Long) => Unit] = None
  )(implicit squartz: Squartz): (Date, (String, String), (String, String)) = {
    schedSimple(func, repeatInterval, repeatUnit, -1,
      startDateOpt, endDateOpt, jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt,
      Some(lockFunc)
    )
  }

  def schedSimpleForever(
    func: (JobExecutionContext) => Unit,
    repeatInterval: Int,
    repeatUnit: Time,

    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None,
    lockFuncOpt: Option[(Long) => Unit] = None
  )(implicit squartz: Squartz): (Date, (String, String), (String, String)) = {
    schedSimple(func, repeatInterval, repeatUnit, -1,
      startDateOpt, endDateOpt, jobIdentOpt, triggerIdentOpt,
      jobDataMapOpt, triggerDataMapOpt,
      lockFuncOpt
    )
  }

  def schedSimpleOnce(
    func: (JobExecutionContext) => Unit,
    date: Date,

    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None,
    lockFuncOpt: Option[(Long) => Unit] = None
  )(implicit squartz: Squartz) = {
    schedSimple(
      func,
      0,
      SECONDS,
      1,
      Some(date),
      None,
      jobIdentOpt,
      triggerIdentOpt,
      jobDataMapOpt,
      triggerDataMapOpt,
      lockFuncOpt
    )
  }

  def schedSimple(
    func: (JobExecutionContext) => Unit,
    repeatInterval: Int,
    repeatUnit: Time,
    repeatCount: Int,
    
    startDateOpt: Option[Date] = None,
    endDateOpt: Option[Date] = None,
    jobIdentOpt: Option[(String, Option[String])] = None,
    triggerIdentOpt: Option[(String, Option[String])] = None,
    jobDataMapOpt: Option[Map[String,Any]] = None,
    triggerDataMapOpt: Option[Map[String,Any]] = None,
    lockFuncOpt: Option[(Long) => Unit] = None
  )(implicit squartz: Squartz): (Date, (String, String), (String, String)) = {
    val builder = repeatUnit match {
      case SECONDS =>
        if(repeatCount >= 0) {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatSecondlyForTotalCount(repeatCount, repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatSecondlyForTotalCount(repeatCount, repeatInterval, func)
          }
        } else {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatSecondlyForever(repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatSecondlyForever(repeatInterval, func)
          }
        }
      case MINUTES =>
        if(repeatCount >= 0) {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatMinutelyForTotalCount(repeatCount, repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatMinutelyForTotalCount(repeatCount, repeatInterval, func)
          }
        } else {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatMinutelyForever(repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatMinutelyForever(repeatInterval, func)
          }
        }
      case HOURS =>
        if(repeatCount >= 0) {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatHourlyForTotalCount(repeatCount, repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatHourlyForTotalCount(repeatCount, repeatInterval, func)
          }
        } else {
          lockFuncOpt match {
            case Some(lockFunc) =>
              SquartzSimpleBuilder.repeatHourlyForever(repeatInterval, func, lockFunc)
            case None =>
              SquartzSimpleBuilder.repeatHourlyForever(repeatInterval, func)
          }
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
  
  private def runBuilder(builder: SquartzBuilder[_]): (Date, (String, String), (String, String)) = {
    val (schedDate, trigger, jobDetailOpt) = builder.sched
    val triggerKey = trigger.getKey
    val jobKey = trigger.getKey

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

  def shutdown { scheduler.shutdown; this; }
}

case class Time

case object SECONDS extends Time
case object MINUTES extends Time
case object HOURS extends Time

class ScalaJobFactory extends org.quartz.simpl.PropertySettingJobFactory {
  override def newJob(bundle: org.quartz.spi.TriggerFiredBundle, scheduler: Scheduler): Job = {
    val jobDetail = bundle.getJobDetail
    if(jobDetail.getJobClass.equals(classOf[ScalaJob])) {
      val func = bundle.getJobDetail.getJobDataMap.get("scalaFunc").asInstanceOf[Function1[JobExecutionContext,Unit]]
      new ScalaJob(func)
    } else if(jobDetail.getJobClass.equals(classOf[ScalaJobExclusive])) {
      val func = bundle.getJobDetail.getJobDataMap.get("scalaFunc").asInstanceOf[Function1[JobExecutionContext,Unit]]
      val isLockedFunc = bundle.getJobDetail.getJobDataMap.get("scalaFuncIsLocked").asInstanceOf[Function1[Long,Unit]]
      new ScalaJobExclusive(
        func,
        isLockedFunc
      )
    } else {
      super.newJob(bundle, scheduler)
    }
  }
}

class ScalaJob(
  func: (JobExecutionContext) => Unit
) extends Job {
  override def execute(context: JobExecutionContext) {
    func(context)
  }
}

object ScalaJobExclusive {
  protected val locks = scala.collection.mutable.Map[String,(ReentrantLock, Long)]()
}

class ScalaJobExclusive(
  func: (JobExecutionContext) => Unit,
  lockedFunc: (Long) => Unit
) extends Job {

  import ScalaJobExclusive._

  override def execute(context: JobExecutionContext) {
    val jobKey = context.getJobDetail.getKey
    val lockKey = jobKey.getName.toLowerCase + "." + jobKey.getGroup.toLowerCase

    val (lockOpt, lockedSince) = locks.synchronized {
      if(locks.contains(lockKey)) {
        val (lock, lockedSince) = locks(lockKey)
        if(lock.tryLock) {
          val newLockedSince = System.currentTimeMillis
          locks += lockKey -> (lock, newLockedSince)
          (Some(lock), newLockedSince)
        } else {
          (None, lockedSince)
        }
      } else {
        val lock = new ReentrantLock
        lock.lock
        val lockedSince = System.currentTimeMillis
        locks += lockKey -> (lock, lockedSince)
        (Some(lock), lockedSince)
      }
    }

    lockOpt match {
      case Some(lock) =>
        try {
          func(context)
        } finally {
          lock.unlock
        }
      case None =>
        lockedFunc(lockedSince)
    }
  }
}
