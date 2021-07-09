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

import io.prometheus.client.{CollectorRegistry, Counter, Gauge, Histogram, Summary}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

trait MetricMatchers {

  val sameThreadExecutionContext = new ExecutionContext {
    override def execute(runnable: Runnable): Unit = runnable.run()
    override def reportFailure(cause: Throwable): Unit = {}
  }

  implicit class PimpedFuture[T](f:Future[T]) {
    def result():T = Await.result(f, 5.seconds)
  }

  def gauge():Gauge = Gauge.build("example", "help").register(new CollectorRegistry())
  def gauge(label:String):Gauge = Gauge.build("example", "help").labelNames(label).register(new CollectorRegistry())
  def counter():Counter = Counter.build("example", "help").register(new CollectorRegistry())
  def counter(label:String):Counter = Counter.build("example", "help").labelNames(label).register(new CollectorRegistry())
  def histogram():Histogram = Histogram.build("example", "help").register(new CollectorRegistry())
  def histogram(label:String):Histogram = Histogram.build("example", "help").labelNames(label).register(new CollectorRegistry())
  def summary():Summary = Summary.build("example", "help").register(new CollectorRegistry())
  def summary(label:String):Summary = Summary.build("example", "help").labelNames(label).register(new CollectorRegistry())

}
