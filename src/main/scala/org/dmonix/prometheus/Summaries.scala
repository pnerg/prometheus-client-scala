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

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
 * Adds functions related to the ''Summary'' class.
 * @since 1.0
 */
object Summaries {

  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val res = measure(summary) {
   *     //code goes here
   *   }
   * }}}
   * @param summary The summary to which to register the execution time
   * @param block The block of code to execute/measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measure[T](summary:Summary)(block: => T): T = {
    val timer = summary.startTimer()
    try {
      block
    } finally {
      timer.observeDuration()
    }
  }

  /**
   * Measures the time it takes to execute the provided block of code.
   * {{{
   *   val res = measure(summary) {
   *     //code goes here
   *   }
   * }}}
   * @param summary The summary to which to register the execution time
   * @param block The block of code to execute/measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measure[T](summary:Summary.Child)(block: => T): T = {
    val timer = summary.startTimer()
    try {
      block
    } finally {
      timer.observeDuration()
    }
  }

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val fut = measureAsync(summary) {
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param summary The summary to which to register the execution time
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measureAsync[T](summary:Summary)(block: => Future[T])(implicit ec:ExecutionContext): Future[T] = {
    val timer = summary.startTimer()
    block.andThen{case _ => timer.observeDuration()}(ec)
  }

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * {{{
   *   val fut = measureAsync(summary) {
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param summary The summary to which to register the execution time
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measureAsync[T](summary:Summary.Child)(block: => Future[T])(implicit ec:ExecutionContext): Future[T] = {
    val timer = summary.startTimer()
    block.andThen{case _ => timer.observeDuration()}(ec)
  }

  /**
   * Record the duration (as seconds) to the provided histogram.
   * @param summary The summary to which to register the duration
   * @param duration The duration to register
   * @since 1.0
   */
  def record(summary:Summary)(duration: Duration):Unit = summary.observe(duration.toSeconds.toDouble)

  /**
   * Record the duration (as seconds) to the provided histogram.
   * @param summary The summary to which to register the duration
   * @param duration The duration to register
   * @since 1.0
   */
  def record(summary:Summary.Child)(duration: Duration):Unit = summary.observe(duration.toSeconds.toDouble)
}
