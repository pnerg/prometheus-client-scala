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

import org.specs2.mutable.Specification

import scala.concurrent.Future

/**
 * Tests for [[Gauges]]
 */
class GaugesSpec extends Specification with GaugeImplicits with MetricMatchers{

  private val expectedValue = "Nunquam non paratus"

  "A Gauge" >> {
    "must 'measure'" >> {
      val res = Gauges.measure(gauge()) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Gauges.measureAsync(gauge()) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must reset" >> {
      val g = gauge()
      g.inc(123)
      Gauges.reset(g).get() === 0
    }
  }

  "A Gauge.Child" >> {
    "must 'measure'" >> {
      val res = Gauges.measure(gauge("label").labels("xyz")) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Gauges.measureAsync(gauge("label").labels("xyz")) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must reset" >> {
      val g = gauge("label").labels("xyz")
      g.inc(123)
      Gauges.reset(g).get() === 0
    }

  }
}
