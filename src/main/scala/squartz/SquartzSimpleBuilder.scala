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
  def repeatHourlyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever,
    None
  )

  def repeatHourlyForever(squartzFunc: (JobExecutionContext) => Unit)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever,
    Some(squartzFunc)
  )

  def repeatHourlyForever(squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatHourlyForever,
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatHourlyForever(hours: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever(hours),
    None
  )

  def repeatHourlyForever(hours: Int, squartzFunc: (JobExecutionContext) => Unit)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForever(hours),
    Some(squartzFunc)
  )

  def repeatHourlyForever(hours: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatHourlyForever(hours),
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatHourlyForTotalCount(count: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count),
    None
  )

  def repeatHourlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count),
    Some(squartzFunc)
  )

  def repeatHourlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatHourlyForTotalCount(count),
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours),
    None
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int, squartzFunc: (JobExecutionContext) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours),
	    Some(squartzFunc)
  )

  def repeatHourlyForTotalCount(count: Int, hours: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatHourlyForTotalCount(count, hours),
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatMinutelyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever,
    None
  )

  def repeatMinutelyForever(squartzFunc: (JobExecutionContext) => Unit)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever,
    Some(squartzFunc)
  )

  def repeatMinutelyForever(squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatMinutelyForever,
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatMinutelyForever(minutes: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForever(minutes),
    None
  )

  def repeatMinutelyForever(minutes: Int, squartzFunc: (JobExecutionContext) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatMinutelyForever(minutes),
	    Some(squartzFunc)
  )

  def repeatMinutelyForever(minutes: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatMinutelyForever(minutes),
	    Some(squartzFunc),
	    Some(lockFunc)
  )

  def repeatMinutelyForTotalCount(count: Int)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
	  SimpleScheduleBuilder.repeatMinutelyForTotalCount(count),
	  None
  )

  def repeatMinutelyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit)
    (implicit squartz: Squartz)= new SquartzSimpleBuilder(
  	  SimpleScheduleBuilder.repeatMinutelyForTotalCount(count),
  	  Some(squartzFunc)
  )

  def repeatMinutelyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz)= new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatMinutelyForTotalCount(count),
      Some(squartzFunc),
      Some(lockFunc)
  )
  
  def repeatMinutelyForTotalCount(count: Int, minutes: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes),
    None
  )

  def repeatMinutelyForTotalCount(count: Int, minutes: Int, squartzFunc: (JobExecutionContext) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes),
      Some(squartzFunc)
  )

  def repeatMinutelyForTotalCount(count: Int, minutes: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, minutes),
      Some(squartzFunc),
      Some(lockFunc)
  )

  def repeatSecondlyForever()(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever,
    None
  )

  def repeatSecondlyForever(squartzFunc: (JobExecutionContext) => Unit) (implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever,
    Some(squartzFunc)
  )

  def repeatSecondlyForever(squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatSecondlyForever,
      Some(squartzFunc),
      Some(lockFunc)
  )

  def repeatSecondlyForever(seconds: Int) (implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForever(seconds),
    None
  )

  def repeatSecondlyForever(seconds: Int, squartzFunc: (JobExecutionContext) => Unit) 
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatSecondlyForever(seconds),
      Some(squartzFunc)
  )

  def repeatSecondlyForever(seconds: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit)
    (implicit squartz: Squartz)= new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatSecondlyForever(seconds),
      Some(squartzFunc),
      Some(lockFunc)
  )

  def repeatSecondlyForTotalCount(count: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count),
    None
  )

  def repeatSecondlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit)
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatSecondlyForTotalCount(count),
      Some(squartzFunc)
  )

  def repeatSecondlyForTotalCount(count: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit) 
    (implicit squartz: Squartz) = new SquartzSimpleBuilder(
      SimpleScheduleBuilder.repeatSecondlyForTotalCount(count),
      Some(squartzFunc),
      Some(lockFunc)
   )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int)(implicit squartz: Squartz) = new SquartzSimpleBuilder(
    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds),
    None
  )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int, squartzFunc: (JobExecutionContext) => Unit) 
  	(implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds),
	    Some(squartzFunc)
  )

  def repeatSecondlyForTotalCount(count: Int, seconds: Int, squartzFunc: (JobExecutionContext) => Unit, lockFunc: (Long) => Unit) 
  	(implicit squartz: Squartz) = new SquartzSimpleBuilder(
	    SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, seconds),
	    Some(squartzFunc),
	    Some(lockFunc)
  )
}

class SquartzSimpleBuilder(
  scheduleBuilder: SimpleScheduleBuilder,
  squartzFuncOpt: Option[(JobExecutionContext) => Unit] = None,
  squartzLockFuncOpt: Option[(Long) => Unit] = None
)(implicit squartz: Squartz) extends SquartzBuilder[SquartzSimpleBuilder](squartz, squartzFuncOpt, squartzLockFuncOpt) {

  def this()(implicit squartz: Squartz) = this(SimpleScheduleBuilder.simpleSchedule)
  def this(squartzFunc: (JobExecutionContext) => Unit)(implicit squartz: Squartz) = this(SimpleScheduleBuilder.simpleSchedule, Some(squartzFunc))
  def this(
    squartzFunc: (JobExecutionContext) => Unit,
    squartzLockFunc: (Long) => Unit
  )(implicit squartz: Squartz) = this(SimpleScheduleBuilder.simpleSchedule, Some(squartzFunc), Some(squartzLockFunc))

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
