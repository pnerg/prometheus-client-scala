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