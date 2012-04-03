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
import org.quartz.SimpleScheduleBuilder._

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
