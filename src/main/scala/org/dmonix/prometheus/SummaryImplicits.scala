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
import scala.concurrent.duration.{Duration, TimeUnit}

/**
 * Enriches the `Summary` class with new functions.
 *
 * The unit used to report latency/time measurements is decided by an implicitly available `TimeUnit`.<br>
 * If no `TimeUnit` is in scope it defaults to `TimeUnit.SECONDS`. <br>
 * One can either declare an implicit value:
 * {{{
 * implicit val defaultUnit = TimeUnit.MILLISECONDS
 * }}}
 * Or as in the this example import an implicit value:
 * {{{
 *   import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS
 *   val requestLatency:Summary = ...
 *   val result = requestLatency.measure {
 *     // Your code here.
 *     }
 * }}}
 */
trait SummaryImplicits {
  /**
   * Class with extensions for the `Histogram.Builder` class
   * @param underlying The instance to enrich with functions
   * @since 1.0
   */
  implicit class HistogramBuilderDecorator(underlying:Summary.Builder) {
    /**
     * Sets the ''unit'' of the metric.
     * @param unit
     * @return
     */
    def unit(unit:TimeUnit):Summary.Builder = underlying.unit(TimeUnitImplicits.asString(unit))
  }

  /**
   * Class with extensions for the `Summary` class
   * @param underlying The instance to enrich with functions
   * @since 1.0
   */
  implicit class SummaryDecorator(underlying:Summary)(implicit unit:TimeUnit = TimeUnit.SECONDS) {
    /**
     * Measures the time it takes to execute the provided block of code.
     * {{{
     *   val summary:Summary = ...
     *   val res = summary.measure{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.0
     */
    def measure[T](block: => T):T = Measurable.measure(record)(block)

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
     * @param block The function returning a Future which to measure
     * @tparam T
     * @return The future created from the provided function block
     * @since 1.0
     */
    def measureAsync[T](block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record)(block)(ec)

    /**
     * Record the duration in the provided unit.
     * @param duration The duration to register
     * @return itself
     * @since 1.0
     */
    def record(duration: Duration):Summary = {
      underlying.observe(duration.toUnit(unit))
      underlying
    }
  }

  /**
   * Class with extensions for the `Summary.Child` class
   * @param underlying The instance to enrich with functions
   * @since 1.0
   */
  implicit class SummaryChildDecorator(underlying:Summary.Child)(implicit unit:TimeUnit = TimeUnit.SECONDS) {
    /**
     * Measures the time it takes to execute the provided block of code.
     * {{{
     *   val summary:Summary = ...
     *   val res = summary.measure{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.0
     */
    def measure[T](block: => T):T = Measurable.measure(record)(block)

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
     * @param block The function returning a Future which to measure
     * @tparam T
     * @return The future created from the provided function block
     * @since 1.0
     */
    def measureAsync[T](block: => Future[T])(implicit ec:ExecutionContext):Future[T] = Measurable.measureAsync(record)(block)(ec)

    /**
     * Record the duration in the unit as set by ''setDefaultUnit''.
     * @param duration The duration to register
     * @return itself
     * @since 1.0
     */
    def record(duration: Duration):Summary.Child = {
      underlying.observe(duration.toUnit(unit))
      underlying
    }

  }
}