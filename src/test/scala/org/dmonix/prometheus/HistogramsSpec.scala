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

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * Tests for [[Histograms]]
 */
class HistogramsSpec extends Specification with GaugeImplicits with MetricMatchers {
  private val expectedValue = "Vae victis"

  "A Histogram" >> {
    "must 'measure'" >> {
      val res = Histograms.measure(histogram(), TimeUnit.SECONDS) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Histograms.measureAsync(histogram(), TimeUnit.SECONDS) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must 'record'" >> {
      Histograms.record(histogram(), TimeUnit.SECONDS)(5.seconds)
      ok
    }
  }

  "A Histogram.Child" >> {
    "must 'measure'" >> {
      val res = Histograms.measure(histogram("label").labels("xyz"), TimeUnit.SECONDS) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Histograms.measureAsync(histogram("label").labels("xyz"), TimeUnit.SECONDS) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must 'record'" >> {
      Histograms.record(histogram("label").labels("xyz"), TimeUnit.SECONDS)(5.seconds)
      ok
    }
  }
}
