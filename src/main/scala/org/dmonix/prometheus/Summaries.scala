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

import io.prometheus.client.Summary

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
 * Holds functions related to 'Summaries'
 */
object Summaries {
  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val summary:Summary = ...
   *   val res = summary.measure{
   *     //code goes here
   *   }
   * }}}
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.0
   */
  def measure[T](summary: Summary, unit:TimeUnit)(block: => T):T = Measurable.measure(record(summary, unit))(block)

  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val summary:Summary.Child = ...
   *   val res = summary.measure{
   *     //code goes here
   *   }
   * }}}
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.0
   */
  def measure[T](summary: Summary.Child, unit:TimeUnit)(block: => T):T = Measurable.measure(record(summary, unit))(block)

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val summary:Summary = ...
   *   val fut = summary.measureAsync{
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return The future created from the provided function block
   * @since 1.0
   */
  def measureAsync[T](summary: Summary, unit:TimeUnit)(block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record(summary, unit))(block)(ec)

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val summary:Summary.Child = ...
   *   val fut = summary.measureAsync{
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return The future created from the provided function block
   * @since 1.0
   */
  def measureAsync[T](summary: Summary.Child, unit:TimeUnit)(block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record(summary, unit))(block)(ec)

  /**
   * Record the duration in the provided unit.
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param duration The duration to register
   * @return itself
   * @since 1.0
   */
  def record(summary: Summary, unit:TimeUnit)(duration: Duration):Summary = {
    summary.observe(duration.toUnit(unit))
    summary
  }

  /**
   * Record the duration in the provided unit.
   * @param summary The summary to use
   * @param unit The timeunit to use when registering the duration
   * @param duration The duration to register
   * @return itself
   * @since 1.0
   */
  def record(summary: Summary.Child, unit:TimeUnit)(duration: Duration):Summary.Child = {
    summary.observe(duration.toUnit(unit))
    summary
  }
}
