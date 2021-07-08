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
    * [Explicit functions](explicit-functions)
    * [Define TimeUnit for latency measurements](#define-timeunit-for-latency-measurements)
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

# Gauges
The _gauge_ is a metric that can both increase/decrease, e.g. ongoing requests.  
This library adds the possibility to automatically increase/decrease a gauge metric around a function, useful for keeping track on parallel jobs.  
E.g.
```scala
import org.dmonix.prometheus.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

val gauge = Gauge.build("gauge", "Measuring ongoing jobs").register()

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
