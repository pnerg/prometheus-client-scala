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
package org.dmonix

/**
 * Adds a Scala layer on the official [Prometheus Java client](https://github.com/prometheus/client_java) library.
 * The purpose is to elevate on the capabilities of the Scala language and provide more fitting constructs.
 * E.g. lambdas from Java don't translate very well into Scala, so this library provides functional approaches to where the original library uses lambdas.
 * Also for time/latency/gauge measurement purposes this library provides means to measure Futures.
 *
 * E.g. measuring the time spent in a function block.
 * {{{
 * val requestLatency:Histogram = ...
 * val result = requestLatency.measure{
 *   // Your code here.
 * }
 * }}}
 * Or measuring the time to execute a future provided as result by a function.
 * The code snippet below will measure the time from invoking the function block to when the future is completed
 * {{{
 * val requestLatency:Histogram = ...
 * val f:Future[_] = requestLatency.measureAsync{
 *   Future {
 *     // Your code here.
 *   }
 * }
 * }}}
 */
package object prometheus