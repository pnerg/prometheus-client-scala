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

import java.util.concurrent.TimeUnit

/**
 * Implicit values one can import for usage with latency metric measurement.
 *
 * E.g.
 * {{{
 *   import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS
 * }}}
 * @since 1.0
 */
object TimeUnitImplicits {
  implicit final def MICROSECONDS: TimeUnit = TimeUnit.MICROSECONDS
  implicit final def MILLISECONDS: TimeUnit = TimeUnit.MILLISECONDS
  implicit final def SECONDS: TimeUnit = TimeUnit.SECONDS
  implicit final def MINUTES: TimeUnit = TimeUnit.MINUTES
  implicit final def HOURS: TimeUnit = TimeUnit.HOURS
  implicit final def DAYS: TimeUnit = TimeUnit.DAYS

  private[prometheus] def asString(unit:TimeUnit):String = {
    if(unit == TimeUnit.MICROSECONDS)
      "micros"
    else if(unit == TimeUnit.MILLISECONDS)
      "millis"
    else if(unit == TimeUnit.SECONDS)
      "seconds"
    else if(unit == TimeUnit.MINUTES)
      "minutes"
    else if(unit == TimeUnit.HOURS)
      "hours"
    else if(unit == TimeUnit.DAYS)
      "days"
    else //default
      "seconds"
  }
}
