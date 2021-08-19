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
 * Enriches the ''Counter'' class with new functions.
 *
 * @since 1.1
 */
trait CounterImplicits {
  /**
   * Class with extensions for the `Counter` class
   * @param underlying The instance to enrich with functions
   * @since 1.1
   */
  implicit class CounterDecorator(underlying:Counter) {
    /**
     * Automatically increases the counter when the provided function block has executed.
     * {{{
     *   val counter:Counter = ...
     *   counter.incAfter{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.1
     */
    def incAfter[T](block: => T): T = Counters.incAfter(underlying)(block)

    /**
     * Automatically increases the counter when the future has completed.
     * {{{
     *   val counter:Counter = ...
     *   counter.incAfterAsync {
     *     Future {
     *       //code goes here
     *     }
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.1
     */
    def incAfterAsync[T](block: => Future[T])(implicit ec:ExecutionContext): Future[T] = Counters.incAfterAsync(underlying)(block)(ec)
  }

  /**
   * Class with extensions for the `Counter.Child` class
   * @param underlying The instance to enrich with functions
   * @since 1.1
   */
  implicit class CounterChildDecorator(underlying:Counter.Child) {
    /**
     * Automatically increases the counter when the provided function block has executed.
     * {{{
     *   val counter:Counter.Child = ...
     *   counter.incAfter{
     *     //code goes here
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.1
     */
    def incAfter[T](block: => T): T = Counters.incAfter(underlying)(block)

    /**
     * Automatically increases the counter when the future has completed.
     * {{{
     *   val counter:Counter.Child = ...
     *   counter.incAfterAsync {
     *     Future {
     *       //code goes here
     *     }
     *   }
     * }}}
     * @param block The block of code to execute/measure
     * @tparam T
     * @return The result created from the provided function block
     * @since 1.1
     */
    def incAfterAsync[T](block: => Future[T])(implicit ec:ExecutionContext): Future[T] = Counters.incAfterAsync(underlying)(block)(ec)
  }
}
