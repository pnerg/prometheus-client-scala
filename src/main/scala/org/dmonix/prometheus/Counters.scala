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

import io.prometheus.client.Counter

import scala.concurrent.{ExecutionContext, Future}

/**
 * Holds functions related to 'Counters'
 * @since 1.1
 */
object Counters {

  /**
   * Automatically increases the counter when the provided function block has executed.
   * {{{
   *   val counter:Counter = ...
   *   Counters.incAfter(counter){
   *     //code goes here
   *   }
   * }}}
   * @param counter The counter to increase
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.1
   */
  def incAfter[T](counter:Counter)(block: => T): T = Measurable.prePostFunc((), counter.inc())(block)

  /**
   * Automatically increases the counter when the provided function block has executed.
   * {{{
   *   val counter:Counter.Child = ...
   *   Counters.incAfter(counter){
   *     //code goes here
   *   }
   * }}}
   * @param counter The counter to increase
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.1
   */
  def incAfter[T](counter:Counter.Child)(block: => T): T = Measurable.prePostFunc((), counter.inc())(block)

  /**
   * Automatically increases the counter when the future has completed.
   * {{{
   *   val counter:Counter = ...
   *   Counters.incAfterAsync(counter){
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param counter The counter to increase
   * @param block The block of code to execute/measure
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.1
   */
  def incAfterAsync[T](counter:Counter)(block: => Future[T])(implicit ec:ExecutionContext): Future[T] = Measurable.prePostFuncAsync((), counter.inc())(block)(ec)

  /**
   * Automatically increases the counter when the future has completed.
   * {{{
   *   val counter:Counter.Child = ...
   *   Counters.incAfterAsync(counter){
   *     Future {
   *       //code goes here
   *     }
   *   }
   * }}}
   * @param counter The counter to increase
   * @param block The block of code to execute/measure
   * @param ec
   * @tparam T
   * @return The result created from the provided function block
   * @since 1.1
   */
  def incAfterAsync[T](counter:Counter.Child)(block: => Future[T])(implicit ec:ExecutionContext): Future[T] = Measurable.prePostFuncAsync((), counter.inc())(block)(ec)
}
