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

object SquartzCronBuilder {
  def dailyAtHourAndMinute(hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.dailyAtHourAndMinute(hour, minute), None
  )

  def dailyAtHourAndMinute(
      hour: Int, minute: Int, squartzFunc: (JobExecutionContext) => Unit
  )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.dailyAtHourAndMinute(hour, minute), Some(squartzFunc)
  )

  def dailyAtHourAndMinute(
    hour: Int, minute: Int, 
    squartzFunc: (JobExecutionContext) => Unit, squartzLockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.dailyAtHourAndMinute(hour, minute), Some(squartzFunc), Some(squartzLockFunc)
  )

  def monthlyOnDayAndHourAndMinute(dayOfMonth: Int, hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.monthlyOnDayAndHourAndMinute(dayOfMonth, hour, minute), None
  )

  def monthlyOnDayAndHourAndMinute(
    dayOfMonth: Int, hour: Int, minute: Int, 
    squartzFunc: (JobExecutionContext) => Unit
  )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.monthlyOnDayAndHourAndMinute(dayOfMonth, hour, minute), Some(squartzFunc)
  )

  def monthlyOnDayAndHourAndMinute(
      dayOfMonth: Int, hour: Int, minute: Int, 
      squartzFunc: (JobExecutionContext) => Unit, squartzLockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.monthlyOnDayAndHourAndMinute(dayOfMonth, hour, minute), Some(squartzFunc), Some(squartzLockFunc)
  )

  def weeklyOnDayAndHourAndMinute(dayOfWeek: Int, hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayOfWeek, hour, minute), None
  )

  def weeklyOnDayAndHourAndMinute(
      dayOfWeek: Int, hour: Int, minute: Int, 
      squartzFunc: (JobExecutionContext) => Unit
   )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayOfWeek, hour, minute), Some(squartzFunc)
  )

  def weeklyOnDayAndHourAndMinute(
      dayOfWeek: Int, hour: Int, minute: Int, 
      squartzFunc: (JobExecutionContext) => Unit, squartzLockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = new SquartzCronBuilder(
    CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayOfWeek, hour, minute), Some(squartzFunc), Some(squartzLockFunc)
  )
}

class SquartzCronBuilder(  
  scheduleBuilder: CronScheduleBuilder,
  squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None,
  squartzLockFuncOpt: Option[(Long) => Unit] = None
)(implicit squartz: Squartz) extends SquartzBuilder[SquartzCronBuilder](squartz, squartzFuncOpt, squartzLockFuncOpt) {

  def this(cronStr: String)(implicit squartz: Squartz) = this(CronScheduleBuilder.cronSchedule(cronStr), None)
  def this(
    cronStr: String,
    squartzFunc: (JobExecutionContext) => Unit
  )(implicit squartz: Squartz) = this(CronScheduleBuilder.cronSchedule(cronStr), Some(squartzFunc))
  def this(
    cronStr: String,
    squartzFunc: (JobExecutionContext) => Unit,
    squartzLockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = this(CronScheduleBuilder.cronSchedule(cronStr), Some(squartzFunc), Some(squartzLockFunc))


  def scheduleInTimeZone(timezone: java.util.TimeZone): SquartzCronBuilder = {
    scheduleBuilder.inTimeZone(timezone)
    this
  }

  def scheduleWithMisfireHandlingInstructionDoNothing: SquartzCronBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionDoNothing
    this
  }

  def scheduleWithMisfireHandlingInstructionFireAndProceed: SquartzCronBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed
    this
  }

  def scheduleWithMisfireHandlingInstructionIgnoreMisfires: SquartzCronBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires
    this
  }

  override protected def getScheduleBuilder = scheduleBuilder
}
