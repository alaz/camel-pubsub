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
  val ownCounter = new AtomicLong(0)

  def apply(@Header(Exchange.TIMER_COUNTER) counter: Long): Unit = {
    val n = ownCounter.incrementAndGet()
    if (n != counter)
      logger.warn(s"$id $this serial is $counter. We measured $n")

    if (counter % 1000 == 0)
      logger.info(s"$id $counter")
  }
}