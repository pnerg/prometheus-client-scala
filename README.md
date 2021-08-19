[![Build & Test](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml/badge.svg)](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/pnerg/prometheus-client-scala/branch/master/graph/badge.svg?token=IM8MVJCI95)](https://codecov.io/gh/pnerg/prometheus-client-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.dmonix/prometheus-client-scala_2.13/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.dmonix/prometheus-client-scala_2.13)
[![Scaladoc](http://javadoc-badge.appspot.com/org.dmonix/prometheus-client-scala_2.13.svg?label=scaladoc)](http://javadoc-badge.appspot.com/org.dmonix/prometheus-client-scala_2.13)
# Prometheus Client Scala
Adds a Scala layer on the official [Prometheus Java client](https://github.com/prometheus/client_java) library.  
The purpose is to elevate on the capabilities of the Scala language and provide more fitting constructs. 
E.g. lambdas from Java don't translate very well into Scala, so this library provides functional approaches to where the original library uses lambdas.  
Also for time/latency/gauge measurement purposes this library provides means to measure Futures.  

E.g measuring a latency for a lambda function in Java could look like:
```java
Histogram requestLatency =  ....
requestLatency.time(() -> {
  // Your code here.
});
```

This library provides a functional approach for the same purpose:  
```scala
val requestLatency:Histogram = ...
val result = requestLatency.measure {
  // Your code here.
}
```

Table of Contents
=================

* [Usage](#usage)
    * [Using traits to add implicit decorations](#using-traits-to-add-implicit-decorations)
    * [Using import to add implicit decorations](#using-import-to-add-implicit-decorations)
    * [Explicit functions](#explicit-functions)
    * [Define TimeUnit for latency measurements](#define-timeunit-for-latency-measurements)
    * [Runnable example](#runnable-example)
* [Counters](#counters)
* [Gauges](#gauges)
* [Histograms](#histograms)
* [Summaries](#summaries)

# Usage
The library provides a few means to use the functionality
* importing the _implicit_ classes to automatically decorate new functions 
* add the traits with implicit declarations to the class
* explicit usage of functions on utility classes

## Using traits to add implicit decorations
Using any of the traits names _*Implicits_ one can import and use any of the decorated functions.  
E.g. 
```scala
import org.dmonix.prometheus
import io.prometheus.client._
class FooBar extends HistogramImplicits {
  val requestLatency:Histogram = ...
  val result = requestLatency.measure {
    // Your code here.
  }
}
```
There are traits for:
* Gauge  -> GaugeImplicits
* Histogram -> HistogramImplicits
* Summary -> SummaryImplicits

Or one can use the _org.dmonix.prometheus.Implicits_ trait which includes all of the above traits
```scala
import org.dmonix.prometheus
class FooBar extends Implicits
```

## Using import to add implicit decorations
If one doesn't want to use traits on the class one can import the class _org.dmonix.prometheus.Implicits_ which will add the same implicits as if extending the trait _Implicits_.   

```scala
import org.dmonix.prometheus.Implicits._
val requestLatency:Histogram = ...
val result = requestLatency.measure {
  // Your code here.
}
```

## Explicit functions
For those that don't like to use implicit functions there is the option to use the underlying explicit functions which the implicit functions use.    
E.g
```scala
val requestLatency:Histogram = ...
val result = Histograms.measure(requestLatency, TimeUnit.Seconds) {
  // Your code here.
}
```

## Define TimeUnit for latency measurements
For both `Histogram` and `Summary` one can choose the `TimeUnit` for the metric.   
By default the unit is set to `TimeUnit.SECONDS` but this can be changed by either:
* Import the desired unit `import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS`
* Declare an implicit unit in scope `implicit val unit = TimeUnit.SECONDS`

```scala
import io.prometheus.client.Histogram
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS
import scala.concurrent.ExecutionContext.Implicits.global

val latencyHistogram = Histogram.build("latency", "Measuring some random latency").unit(TimeUnit.MILLISECONDS).exponentialBuckets(5, 3, 8).register()

latencyHistogram.record(5.millis)
latencyHistogram.record(1.seconds)

latencyHistogram.measureAsync{
  Future{
    Thread.sleep(123)
  }
}
```

## Runnable example
The class [ManualTests](src/test/scala/org/dmonix/prometheus/ManualTests.scala) is a runnable example which creates a few example metrics and publishes these on _localhost:9095_.  
Start the class and inspect the exposed metrics, will be something like
```scala
# HELP gauge Measuring ongoing jobs
# TYPE gauge gauge
gauge 2.0
# HELP counter_total Measures received jobs
# TYPE counter_total counter
counter_total 2.0
# HELP latency_histogram_millis Measuring some random latency
# TYPE latency_histogram_millis histogram
latency_histogram_millis_bucket{le="5.0",} 1.0
latency_histogram_millis_bucket{le="15.0",} 2.0
latency_histogram_millis_bucket{le="45.0",} 2.0
latency_histogram_millis_bucket{le="135.0",} 4.0
latency_histogram_millis_bucket{le="405.0",} 4.0
latency_histogram_millis_bucket{le="1215.0",} 6.0
latency_histogram_millis_bucket{le="3645.0",} 6.0
latency_histogram_millis_bucket{le="10935.0",} 6.0
latency_histogram_millis_bucket{le="+Inf",} 6.0
latency_histogram_millis_count 6.0
latency_histogram_millis_sum 2242.098991
# HELP latency_summary_millis Measuring some random latency
# TYPE latency_summary_millis summary
latency_summary_millis_count 6.0
latency_summary_millis_sum 2239.113056
# HELP counter_created Measures received jobs
# TYPE counter_created gauge
counter_created 1.629358403176E9
# HELP latency_histogram_millis_created Measuring some random latency
# TYPE latency_histogram_millis_created gauge
latency_histogram_millis_created 1.629358403311E9
# HELP latency_summary_millis_created Measuring some random latency
# TYPE latency_summary_millis_created gauge
latency_summary_millis_created 1.629358403334E9
```

# Counters
The _counter_ is a metric that can only increase, e.g. received requests.  
This library adds the possibility to automatically increase a counter metric around a function, useful for incrementing the counter once a function/job has finished.  
E.g.
```scala
import org.dmonix.prometheus.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

val counter = Counter.build("example", "Measures received jobs").register()

//the counter is automatically increased when exiting the scope
val result:String = counter.incAfter {
  //your code that does something
  "example result"
}

//same as above just not using implicits
val result:String = Counters.incAfter(counter) {
  //your code that does something
  "example result"
}

//the counter is automatically when the Future completes
val result:Future[String] = counter.incAfterAsync {
  Future {
    //your code that does something
    "example result"
  }
}

//same as above just not using implicits
val result:Future[String] = Counters.incAfterAsync(counter) {
  Future {
    //your code that does something
    "example result"
  }
}
```

# Gauges
The _gauge_ is a metric that can both increase/decrease, e.g. ongoing requests.  
This library adds the possibility to automatically increase/decrease a gauge metric around a function, useful for keeping track on parallel jobs.  
E.g.
```scala
import org.dmonix.prometheus.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

val gauge = Gauge.build("example", "Measuring ongoing jobs").register()

//the gauge is automatically increased when entering the scope of 'measure' and decreased when exiting
val result:String = gauge.measure {
  //your code that does something
  "example result"
}

//same as above just not using implicits
val result:String = Gauges.measure(gauge) {
  //your code that does something
  "example result"
}

//the gauge is automatically increased when entering the scope of 'measure' and decreased when the Future completes
val result:Future[String] = gauge.measureAsync {
  Future {
    //your code that does something
    "example result"
  }
}

//same as above just not using implicits
val result:Future[String] = Gauges.measureAsync(gauge) {
  Future {
    //your code that does something
    "example result"
  }
}

// zeroes the gauge
gauge.reset()
```

# Histograms
Histograms are traditionally used to measure latency and presented in histogram format.
This library adds the possibility to automatically measure latency/time around a function and report to the histogram.  
E.g.
```scala
import org.dmonix.prometheus.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

val latencyHistogram = Histogram.build("latency", "Measuring some random latency").unit(TimeUnit.MILLISECONDS).exponentialBuckets(5, 3, 8).register()

//the duration of the measured functions is automatically reported to the histogram upon exit of the scope
val result:String = histogram.measure {
  //your code that does something
  "example result"
}

//same as above just not using implicits
val result:String = Histograms.measure(histogram, TimeUnit.Seconds) {
  //your code that does something
  "example result"
}

//the duration/latency is automatically reported when the Future completes
val result:Future[String] = histogram.measureAsync {
  Future {
    //your code that does something
    "example result"
  }
}

//same as above just not using implicits
val result:Future[String] = Histograms.measureAsync(histogram, TimeUnit.Seconds) {
  Future {
    //your code that does something
    "example result"
  }
}
```
# Summaries
Summaries are very similar to Histogram in that they are used to measure e.g. latencies or received data bytes.  
The way the Summary is reported/exposed to Prometheus differs however from the histogram.  
This library adds the possibility to automatically measure latency/time around a function and report to the summary.  
E.g.
```scala
import org.dmonix.prometheus.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

val latencySummary = Summary.build("latency", "Measuring some random latency").unit(TimeUnit.MILLISECONDS).register()

//the duration of the measured functions is automatically reported to the histogram upon exit of the scope
val result:String = latencySummary.measure {
  //your code that does something
  "example result"
}

//same as above just not using implicits
val result:String = Summaries.measure(latencySummary, TimeUnit.Seconds) {
  //your code that does something
  "example result"
}

//the duration/latency is automatically reported when the Future completes
val result:Future[String] = latencySummary.measureAsync {
  Future {
    //your code that does something
    "example result"
  }
}

//same as above just not using implicits
val result:Future[String] = Summaries.measureAsync(latencySummary, TimeUnit.Seconds) {
  Future {
    //your code that does something
    "example result"
  }
}
```
