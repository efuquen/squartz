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
import org.quartz.JobBuilder._
import org.quartz.TriggerBuilder._

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
