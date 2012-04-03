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

object SquartzSimpleBuilder {
  def repeatHourlyForever = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever,
    None
  )

  def repeatHourlyForever(squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever,
    Some(squartzFunc)
  )

  def repeatHourlyForever(hours: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever(hours),
    None
  )

  def repeatHourlyForever(hours: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever(hours),
    Some(squartzFunc)
  )

  def repeatHourlyForTotalCount(count: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count),
    None
  )

  def repeatHourlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count),
    Some(squartzFunc)
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours),
    None
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours),
    Some(squartzFunc)
  )

  def repeatMinutelyForever = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever,
    None
  )

  def repeatMinutelyForever(squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever,
    Some(squartzFunc)
  )

  def repeatMinutelyForever(minutes: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever(minutes),
    None
  )

  def repeatMinutelyForever(minutes: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever(minutes),
    Some(squartzFunc)
  )

  def repeatMinutelyForTotalCount(count: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count),
    None
  )

  def repeatMinutelyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count),
    Some(squartzFunc)
  )

  def repeatMinutelyForTotalCount(count: Int, minutes: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes),
    None
  )

  def repeatMinutelyForTotalCount(count: Int, minutes: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes),
    Some(squartzFunc)
  )

  def repeatSecondlyForever = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever,
    None
  )

  def repeatSecondlyForever(squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever,
    Some(squartzFunc)
  )

  def repeatSecondlyForever(seconds: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever(seconds),
    None
  )

  def repeatSecondlyForever(seconds: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever(seconds),
    Some(squartzFunc)
  )

  def repeatSecondlyForTotalCount(count: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count),
    None
  )

  def repeatSecondlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count),
    Some(squartzFunc)
  )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds),
    None
  )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int, squartzFunc: (JobExecutionContext) => Unit) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds),
    Some(squartzFunc)
  )
}

class SquartzSimpleBuilder(
  scheduleBuilder: SimpleScheduleBuilder,
  squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None
) extends SquartzBuilder[SquartzSimpleBuilder](squartzFuncOpt) {

  def this() = this(SimpleScheduleBuilder.simpleSchedule)
  def this(squartzFunc: (JobExecutionContext) => Unit) = this(SimpleScheduleBuilder.simpleSchedule, Some(squartzFunc))

  def scheduleRepeatForever: SquartzSimpleBuilder = {
    scheduleBuilder.repeatForever
    this
  }

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

  def scheduleWithMisfireHandlingInstructionFireNow: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionFireNow
    this
  }

  def scheduleWithMisfireHandlingInstructionIgnoreMisfires: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires
    this
  }

  def scheduleWithMisfireHandlingInstructionNextWithExistingCount: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNextWithRemainingCount: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNowWithExistingCount: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount
    this
  }

  def scheduleWithMisfireHandlingInstructionNowWithRemainingCount: SquartzSimpleBuilder = {
    scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount
    this
  }

  def scheduleWithRepeatCount(triggerRepeatCount: Int): SquartzSimpleBuilder = {
    scheduleBuilder.withRepeatCount(triggerRepeatCount)
    this
  }

  override protected def getScheduleBuilder = scheduleBuilder
}
