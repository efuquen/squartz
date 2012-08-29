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

import org.quartz._

object SquartzSimpleBuilder {
  
  def build[A <: Job]()(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](SimpleScheduleBuilder.simpleSchedule)
  
  def repeatHourlyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatHourlyForever
  )

  def repeatHourlyForever[A <: Job](implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
    SimpleScheduleBuilder.repeatHourlyForever
  )

  def repeatHourlyForever(hours: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever(hours)
  )

  def repeatHourlyForever[A <: Job](hours: Int)(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
    SimpleScheduleBuilder.repeatHourlyForever(hours)
  )

  def repeatHourlyForTotalCount(count: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count)
  )

  def repeatHourlyForTotalCount[A <: Job](count: Int)(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count)
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours)
  )

  def repeatHourlyForTotalCount[A <: Job](count: Int, hours: Int)
    (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
	    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours)
  )

  def repeatMinutelyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever
  )

  def repeatMinutelyForever[A <: Job]()(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
    SimpleScheduleBuilder.repeatMinutelyForever
  )

  def repeatMinutelyForever(minutes: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever(minutes)
  )

  def repeatMinutelyForever[A <: Job](minutes: Int)
    (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
	    SimpleScheduleBuilder.repeatMinutelyForever(minutes)
  )

  def repeatMinutelyForTotalCount(count: Int)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
  	  SimpleScheduleBuilder.repeatMinutelyForTotalCount(count)
  )

  def repeatMinutelyForTotalCount[A <: Job](count: Int)
    (implicit squartz: Squartz, mA: Manifest[A])= new SquartzSimpleBuilder[A](
  	  SimpleScheduleBuilder.repeatMinutelyForTotalCount(count)
  )

  def repeatMinutelyForTotalCount(count: Int, minutes: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes)
  )

  def repeatMinutelyForTotalCount[A <: Job](count: Int, minutes: Int)
    (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
      SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes)
  )

  def repeatSecondlyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatSecondlyForever
  )

  def repeatSecondlyForever[A <: Job]() (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
    SimpleScheduleBuilder.repeatSecondlyForever
  )

  def repeatSecondlyForever(seconds: Int) (implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatSecondlyForever(seconds)
  )

  def repeatSecondlyForever[A <: Job](seconds: Int) 
    (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
      SimpleScheduleBuilder.repeatSecondlyForever(seconds)
  )

  def repeatSecondlyForTotalCount(count: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count)
  )

  def repeatSecondlyForTotalCount[A <: Job](count: Int)
    (implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
      SimpleScheduleBuilder.repeatSecondlyForTotalCount(count)
  )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder[NoJob](
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds)
  )

  def repeatSecondlyForTotalCount[A <: Job](count: Int, seconds: Int) 
  	(implicit squartz: Squartz, mA: Manifest[A]) = new SquartzSimpleBuilder[A](
	    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds)
  )
}

class SquartzSimpleBuilder[A <: Job](
  scheduleBuilder: SimpleScheduleBuilder
)(implicit squartz: Squartz, mA: Manifest[A]) extends SquartzBuilder[SquartzSimpleBuilder[A], A](squartz) {

  /*def this[A <: Job]()(implicit squartz: Squartz) = this[A](SimpleScheduleBuilder.simpleSchedule)
  def this(squartzJob: Job)(implicit squartz: Squartz) = this(SimpleScheduleBuilder.simpleSchedule, Some(squartzJob))*/

  def scheduleRepeatForever: SquartzSimpleBuilder[A] = {
    scheduleBuilder.repeatForever
    this
  }

  def scheduleWithIntervalInHours(intervalHours: Int): SquartzSimpleBuilder[A] = {
    scheduleBuilder.withIntervalInHours(intervalHours)
    this
  }

  def scheduleWithIntervalInMilliseconds(intervalInMillis: Long): SquartzSimpleBuilder[A] = {
    scheduleBuilder.withIntervalInMilliseconds(intervalInMillis)
    this
  }

  def scheduleWithIntervalInMinutes(intervalInMinutes: Int): SquartzSimpleBuilder[A] = {
    scheduleBuilder.withIntervalInMinutes(intervalInMinutes)
    this
  }

  def scheduleWithIntervalInSeconds(intervalInSeconds: Int): SquartzSimpleBuilder[A] = {
    scheduleBuilder.withIntervalInSeconds(intervalInSeconds)
    this
  }

  def scheduleWithMisfireHandlingInstructionFireNow: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionFireNow
    this
  }

  def scheduleWithMisfireHandlingInstructionIgnoreMisfires: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires
    this
  }

  def scheduleWithMisfireHandlingInstructionNextWithExistingCount: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNextWithRemainingCount: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNowWithExistingCount: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNowWithRemainingCount: SquartzSimpleBuilder[A] = {
    scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount
    this
  }

  def scheduleWithRepeatCount(triggerRepeatCount: Int): SquartzSimpleBuilder[A] = {
    scheduleBuilder.withRepeatCount(triggerRepeatCount)
    this
  }

  override protected def getScheduleBuilder = scheduleBuilder
}
