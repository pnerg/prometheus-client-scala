package org.dmonix.prometheus

import io.prometheus.client.{CollectorRegistry, Gauge, Histogram, Summary}

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
  def histogram():Histogram = Histogram.build("example", "help").register(new CollectorRegistry())
  def summary():Summary = Summary.build("example", "help").register(new CollectorRegistry())

}
