[![Build & Test](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml/badge.svg)](https://github.com/pnerg/prometheus-client-scala/actions/workflows/scala.yml)
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
The library provides a few means to use the functionality, either by importing the _implicit_ classes to automatically decorate new functions or use the explicit functions.

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

## Using explicit functions
If one doesn't fancy using implicit constructs there is the option to use the utility functions directly.   
Under the hood this is what the implicit constructs do, just masking to add some syntactic sugar.   
All utility functions are defined in these classes:  
* Gauge  -> Gauges
* Histogram -> Histograms
* Summary -> Summaries

E.g.
```
import org.dmonix.prometheus._
val requestLatency:Histogram = ...
val result = Histograms.measure(requestLatency){
    // Your code here.
}
```



