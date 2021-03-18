[![Build & Test](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml/badge.svg)](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/pnerg/prometheus-client-scala/branch/master/graph/badge.svg?token=IM8MVJCI95)](https://codecov.io/gh/pnerg/prometheus-client-scala)
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

# Usage
The library provides a few means to use the functionality, either by importing the _implicit_ classes to automatically decorate new functions or to add the traits with implicit declarations to the class.

## Using traits to add implicit decorations
Using any of the traits names _*Implicits_ one can import and use any of the decorated functions.  
E.g. 
```
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
```
import org.dmonix.prometheus
class FooBar extends Implicits
```

## Using import to add implicit decorations
If one doesn't want to use traits on the class one can import the class _org.dmonix.prometheus.Implicits_ which will add the same implicits as if extending the trait _Implicits_.   

```
import org.dmonix.prometheus.Implicits._
val requestLatency:Histogram = ...
val result = requestLatency.measure {
  // Your code here.
}
```

## Define TimeUnit for latency measurements
For both `Histogram` and `Summary` one can choose the `TimeUnit` for the metric.   
By default the unit is set to `TimeUnit.SECONDS` but this can be changed by either:
* Import the desired unit `import org.dmonix.prometheus.TimeUnitImplicits.MILLISECONDS`
* Declare an implicit unit in scope `implicit val unit = TimeUnit.SECONDS`

```
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
