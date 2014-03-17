import org.apache.camel.builder.RouteBuilder

class Gen extends RouteBuilder {
  override def configure {
    from("timer:generator?delay=1000&period=1")
      .to(Bus.Endpoint)
  }
}
