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

/**
 * Object with all the metrics traits.
 *
 * Used for importing the enriched traits as opposed to adding one of the traits to the class.
 * {{{
 * import org.dmonix.prometheus.Implicits._
 * val requestLatency:Histogram = ...
 * val result = requestLatency.measure {
 *  // Your code here.
 * }
 * }}}
 */
object Implicits extends Implicits

/**
 * Combines all the individual metric traits ('*Implicits').
 *
 * Example of usage:
 * {{{
 * import org.dmonix.prometheus
 * class FooBar extends Implicits
 * }}}
 */
trait Implicits extends GaugeImplicits with HistogramImplicits with SummaryImplicits