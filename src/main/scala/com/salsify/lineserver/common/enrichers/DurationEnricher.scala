package com.salsify.lineserver.common.enrichers

import scala.concurrent.duration.Duration
import java.time.{Duration => JDuration}

object DurationEnricher {
  implicit class RichJavaDuration(duration: JDuration) {

    /**
      * Converts a [[JDuration]] into a [[Duration]].
      */
    def asScala: Duration = Duration.fromNanos(duration.toNanos)
  }
}
