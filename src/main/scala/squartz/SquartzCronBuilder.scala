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
  
  def build[A <: Job](cronStr: String)(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzCronBuilder(CronScheduleBuilder.cronSchedule(cronStr))
  
  def dailyAtHourAndMinute(hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder[NoJob](
    CronScheduleBuilder.dailyAtHourAndMinute(hour, minute) 
  )

  def dailyAtHourAndMinute[A <: Job](
      hour: Int, minute: Int
  )(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzCronBuilder[A](
    CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
  )

  def monthlyOnDayAndHourAndMinute(dayOfMonth: Int, hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder[NoJob](
    CronScheduleBuilder.monthlyOnDayAndHourAndMinute(dayOfMonth, hour, minute) 
  )

  def monthlyOnDayAndHourAndMinute[A <: Job](
    dayOfMonth: Int, hour: Int, minute: Int
  )(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzCronBuilder[A](
    CronScheduleBuilder.monthlyOnDayAndHourAndMinute(dayOfMonth, hour, minute)
  )

  def weeklyOnDayAndHourAndMinute(dayOfWeek: Int, hour: Int, minute: Int)(implicit squartz: Squartz) = new SquartzCronBuilder[NoJob](
    CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayOfWeek, hour, minute) 
  )

  def weeklyOnDayAndHourAndMinute[A <: Job](
      dayOfWeek: Int, hour: Int, minute: Int
   )(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzCronBuilder[A](
    CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayOfWeek, hour, minute)
  )
  
}

class SquartzCronBuilder[A <: Job](  
  scheduleBuilder: CronScheduleBuilder
)(implicit squartz: Squartz, mA: Manifest[A]) extends SquartzBuilder[SquartzCronBuilder[A], A](squartz) {

  /*def this(cronStr: String)(implicit squartz: Squartz) = this(CronScheduleBuilder.cronSchedule(cronStr), None)
  def this(
    cronStr: String,
    squartzJob: Job
  )(implicit squartz: Squartz) = this(CronScheduleBuilder.cronSchedule(cronStr), Some(squartzJob))*/
  
  def scheduleInTimeZone(timezone: java.util.TimeZone): SquartzCronBuilder[A] = {
    scheduleBuilder.inTimeZone(timezone)
    this
  }

  def scheduleWithMisfireHandlingInstructionDoNothing: SquartzCronBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionDoNothing
    this
  }

  def scheduleWithMisfireHandlingInstructionFireAndProceed: SquartzCronBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed
    this
  }

  def scheduleWithMisfireHandlingInstructionIgnoreMisfires: SquartzCronBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires
    this
  }

  override protected def getScheduleBuilder = scheduleBuilder
}
