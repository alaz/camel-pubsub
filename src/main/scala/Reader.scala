import org.apache.camel.builder.RouteBuilder

class Reader(val id: String) extends RouteBuilder {
  override def configure {
    from(Bus.Endpoint)
      .bean(new Handler(id))
  }
}

class ReaderA extends Reader("A")
class ReaderB extends Reader("B")

class Handler(val id: String) {
  import org.slf4j.LoggerFactory
  import org.apache.camel.{Exchange, Header}
  import java.util.concurrent.atomic.AtomicLong

  val logger = LoggerFactory.getLogger(getClass)

  // we have own counter inside the processor
  val ownCounter = new AtomicLong(0)

  // we know `timer` generates the numbers sequentially
  def apply(@Header(Exchange.TIMER_COUNTER) counter: Long): Unit = {
    // we increment our own counter
    val n = ownCounter.incrementAndGet()

    // and we expect these numbers equal. It would mean another thread is involved
    // if the numbers are different
    if (n != counter)
      logger.warn(s"$id $this serial is $counter. We measured $n")

    if (counter % 1000 == 0)
      logger.info(s"$id $counter")
  }
}