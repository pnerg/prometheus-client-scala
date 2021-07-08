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

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

private[prometheus] object Measurable {

  /**
   * Measures the time it takes to execute the provided block of code.
   * @param reportFunc The function to report the measured duration to
   * @param block The block of code to execute/measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measure[T](reportFunc:Duration => Unit)(block: => T): T = {
    val start = System.nanoTime()
    try {
      block
    } finally {
      reportFunc.apply(durationFrom(start))
    }
  }

  /**
   * Measures the time it takes to execute the Future resulting from the provided block of code.
   * @param reportFunc The function to report the measured duration to
   * @param block The function returning a Future which to measure
   * @tparam T
   * @return
   * @since 1.0
   */
  def measureAsync[T](reportFunc:Duration => Unit)(block: => Future[T])(implicit ec:ExecutionContext): Future[T] = {
    val start = System.nanoTime()
    block.andThen{case _ => reportFunc.apply(durationFrom(start))}(ec)
  }

  /**
   * Returns the duration measured in nanos from the provided start time to 'now'
   * @param start The nano start time
   * @return
   */
  private def durationFrom(start:Long):Duration = Duration.fromNanos(System.nanoTime()-start)
}
