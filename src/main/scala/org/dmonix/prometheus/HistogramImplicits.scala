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

import io.prometheus.client.{Gauge, Histogram}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
 * Enriches the ''Histogram'' class with new functions.
 * @since 1.0
 */
trait HistogramImplicits {
  /**
   * Class with extensions for the `Histogram` class
   * @param underlying The instance to enrich with functions
   * @since 1.0
   */
  implicit class HistogramDecorator(underlying:Histogram) {
    /**
     * Measures the time it takes to execute the provided block of code.
     * {{{
     *   val histogram:Histogram = ...
     *   val res = histogram.measure{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.0
     */
    def measure[T](block: => T):T = Histograms.measure(underlying)(block)

    /**
     * Measures the time it takes to execute the Future resulting from the provided block of code.
     * {{{
     *   val histogram:Histogram = ...
     *   val fut = histogram.measureAsync{
     *     Future {
     *       //code goes here
     *     }
     *   }
     * }}}
     * @param block The function returning a Future which to measure
     * @tparam T
     * @return The future created from the provided function block
     * @since 1.0
     */
    def measureAsync[T](block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Histograms.measureAsync(underlying)(block)(ec)

    /**
     * Record the duration (as seconds) to the the histogram.
     * @param duration The duration to register
     * @return itself
     * @since 1.0
     */
    def record(duration: Duration):Histogram = {
      Histograms.record(underlying)(duration)
      underlying
    }
  }

  /**
   * Class with extensions for the `Histogram` class
   * @param underlying The instance to enrich with functions
   * @since 1.0
   */
  implicit class HistogramChildDecorator(underlying:Histogram.Child) {
    /**
     * Measures the time it takes to execute the provided block of code.
     * {{{
     *   val histogram:Histogram = ...
     *   val res = histogram.measure{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.0
     */
    def measure[T](block: => T):T = Histograms.measure(underlying)(block)

    /**
     * Measures the time it takes to execute the Future resulting from the provided block of code.
     * {{{
     *   val histogram:Histogram = ...
     *   val fut = histogram.measureAsync{
     *     Future {
     *       //code goes here
     *     }
     *   }
     * }}}
     * @param block The function returning a Future which to measure
     * @tparam T
     * @return The future created from the provided function block
     * @since 1.0
     */
    def measureAsync[T](block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Histograms.measureAsync(underlying)(block)(ec)

    /**
     * Record the duration (as seconds) to the the histogram.
     * @param duration The duration to register
     * @return itself
     * @since 1.0
     */
    def record(duration: Duration):Histogram.Child = {
      Histograms.record(underlying)(duration)
      underlying
    }
  }
}
