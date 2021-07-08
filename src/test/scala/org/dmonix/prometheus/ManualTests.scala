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

import io.prometheus.client.{Gauge, Histogram, Summary}
import io.prometheus.client.exporter.HTTPServer

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * Some manual tests to play around with metrics and see the results
 */
object ManualTests extends App with Implicits {
  histogramExample()
  gaugeExample()
  summaryExample()

  new HTTPServer(9095)
  println("Go check metrics at http://localhost:9095")
  Thread.currentThread().join()

  private def gaugeExample(): Unit = {
    import java.util.concurrent.Executors
    import java.util.concurrent.TimeUnit
    val gauge = Gauge.build("gauge", "Measuring ongoing jobs").register()
    def worker():Runnable = new Runnable {
      override def run(): Unit = {
        gauge.measure {
          //fake some job
          Thread.sleep(5000)
        }
      }
    }
    val scheduler = Executors.newScheduledThreadPool(2)
    scheduler.scheduleWithFixedDelay(worker(), 1, 1, TimeUnit.SECONDS)
    scheduler.scheduleWithFixedDelay(worker(), 1, 4, TimeUnit.SECONDS)
  }

  private def histogramExample(): Unit = {
    import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS

    import scala.concurrent.ExecutionContext.Implicits.global
    val latencyHistogram = Histogram.build("latency_histogram", "Measuring some random latency").unit(TimeUnit.MILLISECONDS).exponentialBuckets(5, 3, 8).register()

    latencyHistogram.record(5.millis)
    latencyHistogram.record(10.millis)
    latencyHistogram.record(100.millis)
    latencyHistogram.record(1000.millis)
    latencyHistogram.record(1.seconds)

    latencyHistogram.measureAsync{
      Future{
        Thread.sleep(123)
      }
    }
  }

  private def summaryExample(): Unit = {
    import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS

    import scala.concurrent.ExecutionContext.Implicits.global
    val latencySummary = Summary.build("latency_summary", "Measuring some random latency").unit(TimeUnit.MILLISECONDS).register()

    latencySummary.record(5.millis)
    latencySummary.record(10.millis)
    latencySummary.record(100.millis)
    latencySummary.record(1000.millis)
    latencySummary.record(1.seconds)

    latencySummary.measureAsync{
      Future{
        Thread.sleep(123)
      }
    }
  }
}
