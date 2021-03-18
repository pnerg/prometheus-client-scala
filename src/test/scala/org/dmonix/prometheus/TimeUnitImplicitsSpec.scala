/**
 *  Copyright 2021 Peter Nerg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dmonix.prometheus

import org.specs2.mutable.Specification

import java.util.concurrent.TimeUnit

/**
 * Tests for [[TimeUnitImplicits]]
 */
class TimeUnitImplicitsSpec extends Specification {
  "implicit TimeUnit defs" >> {
    "MICROS" >> {
      TimeUnitImplicits.MICROSECONDS === TimeUnit.MICROSECONDS
    }
    "MILLISECONDS" >> {
      TimeUnitImplicits.MILLISECONDS === TimeUnit.MILLISECONDS
    }
    "SECONDS" >> {
      TimeUnitImplicits.SECONDS === TimeUnit.SECONDS
    }
    "MINUTES" >> {
      TimeUnitImplicits.MINUTES === TimeUnit.MINUTES
    }
    "HOURS" >> {
      TimeUnitImplicits.HOURS === TimeUnit.HOURS
    }
    "DAYS" >> {
      TimeUnitImplicits.DAYS === TimeUnit.DAYS
    }
  }

  "asString" >> {
    "with 'MICROSECONDS'" >> {
      TimeUnitImplicits.asString(TimeUnit.MICROSECONDS) === "micros"
    }
    "with 'MILLISECONDS'" >> {
      TimeUnitImplicits.asString(TimeUnit.MILLISECONDS) === "millis"
    }
    "with 'SECONDS'" >> {
      TimeUnitImplicits.asString(TimeUnit.SECONDS) === "seconds"
    }
    "with 'MINUTES'" >> {
      TimeUnitImplicits.asString(TimeUnit.MINUTES) === "minutes"
    }
    "with 'HOURS'" >> {
      TimeUnitImplicits.asString(TimeUnit.HOURS) === "hours"
    }
    "with 'DAYS'" >> {
      TimeUnitImplicits.asString(TimeUnit.DAYS) === "days"
    }
  }
}
