package org.squartz

import Squartz._
import org.scalatest.FunSuite

import org.quartz.Job
import org.quartz.JobExecutionContext

class DuplicateTestJob extends Job {
   override def execute(context: JobExecutionContext) { 
   }
}

class DuplicateJobSuite extends FunSuite {
  test("duplicate jobs") {
    implicit val squartz = Squartz.build.start

    try {
      schedSimple[DuplicateTestJob](
        10,
        SECONDS,
        -1,
        jobIdentOpt = Some("test", None)
      )

      intercept[org.quartz.ObjectAlreadyExistsException] {
        schedSimple[DuplicateTestJob](
          10,
          SECONDS,
          -1,
          jobIdentOpt = Some("test", None)
        )
      }
    } finally { squartz.shutdown }
  }
}
