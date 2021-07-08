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

class SummariesSpec extends Specification with SummaryImplicits with MetricMatchers {
  private val expectedValue = "Sic semper tyrannis"

  "A Summary" >> {
    "must 'measure'" >> {
      val res = Summaries.measure(summary(), TimeUnit.SECONDS) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Summaries.measureAsync(summary(), TimeUnit.SECONDS) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must 'record'" >> {
      Summaries.record(summary(), TimeUnit.SECONDS)(5.seconds)
      ok
    }
  }

  "A Summary.Child" >> {
    "must 'measure'" >> {
      val res = Summaries.measure(summary("label").labels("xyz"), TimeUnit.SECONDS) {
        expectedValue
      }
      res === expectedValue
    }

    "must 'measureAsync'" >> {
      implicit val ec = sameThreadExecutionContext
      val f = Summaries.measureAsync(summary("label").labels("xyz"), TimeUnit.SECONDS) {
        Future {
          expectedValue
        }
      }
      f.result() === expectedValue
    }

    "must 'record'" >> {
      Summaries.record(summary("label").labels("xyz"), TimeUnit.SECONDS)(5.seconds)
      ok
    }
  }
}
