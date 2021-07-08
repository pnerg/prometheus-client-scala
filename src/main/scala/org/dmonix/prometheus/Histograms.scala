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

import io.prometheus.client.Histogram

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
 * Holds functions related to 'Histograms'
 */
object Histograms {
  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val histogram:Histogram = ...
   *   val res = Histograms.measure(histogram, TimeUnit.SECONDS){
   *     //code goes here
   *   }
   * }}}
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.0
   */
  def measure[T](histogram: Histogram, unit:TimeUnit)(block: => T):T = Measurable.measure(record(histogram, unit))(block)

  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val histogram:Histogram.Child = ...
   *   val res = Histograms.measure(histogram, TimeUnit.SECONDS){
   *     //code goes here
   *   }
   * }}}
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.0
   */
  def measure[T](histogram: Histogram.Child, unit:TimeUnit)(block: => T):T = Measurable.measure(record(histogram, unit))(block)

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val histogram:Histogram = ...
   *   val fut = Histograms.measureAsync(histogram, TimeUnit.SECONDS){
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return The future created from the provided function block
   * @since 1.0
   */
  def measureAsync[T](histogram: Histogram, unit:TimeUnit)(block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record(histogram, unit))(block)(ec)

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val histogram:Histogram.Child = ...
   *   val fut = Histograms.measureAsync(histogram, TimeUnit.SECONDS){
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return The future created from the provided function block
   * @since 1.0
   */
  def measureAsync[T](histogram: Histogram.Child, unit:TimeUnit)(block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record(histogram, unit))(block)(ec)

  /**
   * Record the duration in the unit as set by seen by the implicit unit visible to this class.
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param duration The duration to register
   * @return itself
   * @since 1.0
   */
  def record(histogram: Histogram, unit:TimeUnit)(duration: Duration):Histogram = {
    histogram.observe(duration.toUnit(unit))
    histogram
  }

  /**
   * Record the duration in the unit as set by seen by the implicit unit visible to this class.
   * @param histogram The histogram to use
   * @param unit The timeunit to use when registering the duration
   * @param duration The duration to register
   * @return itself
   * @since 1.0
   */
  def record(histogram: Histogram.Child, unit:TimeUnit)(duration: Duration):Histogram.Child = {
    histogram.observe(duration.toUnit(unit))
    histogram
  }
}
