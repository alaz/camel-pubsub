# Publish-subscribe in Apache Camel

If you'd like to use publish-subscribe pattern in Apache Camel application, you should not
use the suggested `seda:queue?multipleConsumers=true` approach, described
[here](http://camel.apache.org/seda#SEDA-UsingmultipleConsumers). There is a "by design" flaw
that may cost you a lot.

```
  from("seda:queue?multipleConsumers=true")
    .bean(Processor1)

  from("seda:queue?multipleConsumers=true")
    .bean(Processor2)
```

`multipleConsumers` implementation makes use of `multicast` and `parallelProcessing` under the hood
and this combination utilizes the default thread pool to dispatch exchanges to the underlying endpoints.
The problem here is that **every subscriber** becomes parallel implicitly. You didn't expect it, but
there will be several threads running `Processor1` concurrently and several threads running `Processor2`
concurrently as well.

Hence, if you have inner state inside either of them which relies on the order of incoming exchanges –
you are in trouble.

By the way, this could be a bigger surprise if you had only one subscriber for some time and everything
was working. It may (suddenly) break when you add another one.

## Demonstration

This project is a simple demonstration of this behavior - review it, look into the comments and try to run:

```sh
$ sbt
> run -ac routes.xml
```

## Bug report

The bug report is [here](https://issues.apache.org/jira/browse/CAMEL-7302).

The Camel team will not fix it,
they claimed this behavior is "as designed".

# Workarounds

## threads(1) – NO

You would probably want to make execution sequential:

```
  from("seda:queue?multipleConsumers=true")
    .threads(1)
    .bean(Processor1)
```

This will not help, b/c this whole fragment gets called concurrently.

## direct:seq – NO

```
  from("seda:queue?multipleConsumers=true")
    .to("direct:seq")

  from("direct:seq")
    .bean(Processor1)
```

This will make execution sequential, but it cannot guarantee the order of exchanges is intact.

## Disruptor – YES

What seems the real fix is using [Disruptor](https://camel.apache.org/disruptor.html) component, see the
[PR](https://github.com/alaz/camel-pubsub/pull/1) for required changes. Fetch the branch and try running it.