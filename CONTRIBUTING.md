sbt # Contributing Guide

This project is developed in Scala and [scala-sbt](https://www.scala-sbt.org/) as the build tool.

### Requirements

* Sbt 1.2.x.
* Scala 2.12.8.
* Docker

### Development Guide

The following sections briefly describe how to contribute to the project.

#### Building and running the project.

To install the project, execute the following command in the root of the project:

```
$ ./build.sh
```

This builds and installs the following Docker images in your local Docker registry: `lineserver:latest` and 
`lineserver:0.1.0-SNAPSHOT`.

To run the project execute the following command in the root of the project:
```
$ ./run <file-location>
```

Where `<file-location>` is the location of the file that is going to be available in the server.

This command will launch a server with 2 clients, 2 shards and one load balancer. The server will be ready once you see
the following line:

```
client-primary      | 2019-03-24 15:57:13 INFO  c.s.lineserver.client.ClientServer - Server available at 0.0.0.0:8080
```

At this stage you may access the client's REST API:

```bash
$ curl -I -XGET "localhost:8080/lines/1000"
```

*Note*: You will find more targets in the `Makefile` that these scripts rely on. Each target is contextually commented
in the file.

#### Future work

The current project took 2 weeks of development time. 

The following list summarizes the tasks that would have been explored (ordered) with unlimited time and resources:
1. Compression/decompression of HTTP requests/responses ([link](https://doc.akka.io/docs/akka-http/current/common/encoding.html)).
2. Increase code coverage through unit tests.
3. Add system tests using (but not limited to) [test containers](https://www.testcontainers.org/).
4. Add performance tests using (but not limited to) [Gatling](https://gatling.io/).
5. Support HTTPS.
6. Document the Rest API using [swagger](https://swagger.io/) ([example](https://blog.knoldus.com/swagger-ui-with-akka-http/)).
7. Avoid lazy construction of `ClientResource` in `ClientRoutes`. Despite its low impact and priority, it is technical debt.
8. Remove lock in `ShardResource` as it only exists to solve a minor issue that only impacts startup. 
   Despite its low impact and priority, it is technical debt.
9. Investigate how to reduce the docker image size.

Depending on the performance tests, two additional points would be tackled:
1. *Iff* communication between `client` and `shards` reveals to be the bottleneck and critical, explore alternative
   communication strategies (e.g., TCP directly using [Akka](https://doc.akka.io/docs/akka/2.5/io-tcp.html)). 
2. If memory becomes a bottleneck in the `shards`, consider compressing the Strings before storing them.

#### Comments on the current state

Positive: 
* Simple system design that allows scaling both vertical and horizontal the system to accommodate either bigger files
  or more clients.
* Decoupling shards from the client.
* Capability of launching either a shard or a client from the same application by changing the configuration file.
* Lines Supplier Abstraction in package `com.salsify.lineserver.client.input`. This allows easily extending the system
  to support reading large files from an S3 bucket.
* Usage of cache in the `client` to avoid expensive network requests to a `shard`.
* Backpressure queue (see `ShardHttpClient`) to handle spikes.
* Separation of concerns (through packaging and abstractions)
* Small classes.

Negative:
* Could have more unit tests. For example: `<X>Config` classes and `<X>Factory` classes.
* The `LinesManagerFactory` abstraction that accommodates different strategies to manage shards is overkill at this stage.
* Lazy construction of `ClientResource` in `ClientRoutes`.
* Usage of a lock in `ShardResource` in this use-case. It is not required as there are no updates.
* Code smell in `class ClientRoutesSpec extends BaseSpec with ClientRoutes`

## External libraries

In the `project/dependencies` you will find the libraries used to develop this project:

* [Lightbend Config](https://github.com/lightbend/config): Flexible and widely used configuration library in Scala.
* [Logback](https://logback.qos.ch/) and [scala-logging](https://github.com/lightbend/scala-logging): Convenient and fast
  logging libraries.
* [Akka HTTP](https://doc.akka.io/docs/akka-http/current/): Widely used server-side and client-side HTTP server that was
  chosen due to its adoption in the production environment with strict performance requirements.

These projects are all open source.


### Relevant resources

The following list contains the most relevant websites used through the development of this project.

Firstly, I investigated more on the issue and learned several topics on the way:
* [https://www.techopedia.com/definition/1825/distributed-file-system-dfs]
* [https://en.wikipedia.org/wiki/Comparison_of_distributed_file_systems]
* [https://en.wikipedia.org/wiki/Clustered_file_system]
* [https://www.techopedia.com/definition/1825/distributed-file-system-dfs]
* [https://en.wikipedia.org/wiki/Comparison_of_distributed_file_systems]
* [https://www.quora.com/What-is-the-best-distributed-file-store-or-file-system-for-large-objects]

The key finding was the following project: [https://github.com/chrislusf/seaweedfs] accompanied by the following 
[paper](https://www.usenix.org/legacy/event/osdi10/tech/full_papers/Beaver.pdf). The paper is very interesting as it
explored the alternative approach taken by Facebook to scale how it handles photos. However, this project use case is
different as we only want to distribute key-value pairs. My search ended on object stores and key-value stores:
* [https://en.wikipedia.org/wiki/Object_storage]
* [https://www.quora.com/What-are-good-ways-to-design-a-distributed-key-value-store]
* [https://docs.microsoft.com/en-us/azure/architecture/guide/technology-choices/data-store-overview]
* [https://medium.com/@siddontang/build-up-a-high-availability-distributed-key-value-store-b4e02bc46e9e]

I ended reading more about Redis:
* [https://www.credera.com/blog/technology-insights/java/redis-explained-5-minutes-less/]
* [http://qnimate.com/overview-of-redis-architecture/]
* [https://redis.io/presentation/Redis_Cluster.pdf]
* [https://redis.io/topics/partitioning]
* [https://github.com/twitter/twemproxy]
* [https://www.linkedin.com/pulse/memcached-vs-redis-which-one-pick-ranjeet-vimal/]


I also found alternatives:
* [https://www.g2crowd.com/products/arangodb/reviews]
* [https://aws.amazon.com/memcached/]
* 

Taking these technologies as inspiration, the project inspired on the Redis shard's structure and in-memory philosophy. 
Regarding development itself, the following pages were visited.

Scala:
* [https://maxondev.com/scala-preconditions-assert-assume-require-ensuring/]
* [https://medium.com/@hussachai/scalas-immutable-collections-can-be-slow-as-a-snail-da6fc24bc688]
* [https://alvinalexander.com/scala/scala-file-reading-performance-getlines]
* [https://nordicapis.com/8-frameworks-to-build-a-web-api-in-scala/]
* [http://www.beyondthelines.net/computing/scala-future-and-execution-context/]
* [http://www.scalatest.org/]
* [https://lucianomolinari.com/2016/08/07/testing-future-objects-scalatest/]


Akka:
* [https://doc.akka.io/docs/akka-http/current/index.html]
* [https://chariotsolutions.com/blog/post/simply-explained-akka-streams-backpressure/]
* [https://www.lightbend.com/blog/understanding-akka-streams-back-pressure-and-asynchronous-architectures]
* [https://www.slideshare.net/ZalandoTech/building-a-reactive-restful-api-with-akka-http-slick]
* [https://doc.akka.io/docs/akka-http/current/common/caching.html]
* [https://doc.akka.io/docs/akka-http/current/client-side/request-level.html]
* [https://doc.akka.io/docs/akka-http/current/routing-dsl/exception-handling.html]
* [https://opencredo.com/blogs/introduction-to-akka-streams-getting-started/]
* [https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/caching-directives/cache.html]
* [https://github.com/akka/akka-http/tree/v10.1.7/akka-http-tests/src/test/scala/akka/http/scaladsl/server/directives]
* [https://doc.akka.io/docs/akka-http/current/common/encoding.html]
* [https://www.gregbeech.com/2018/04/08/akka-http-client-pooling-and-parallelism/]
* [https://github.com/akka/akka-http/blob/master/docs/src/test/scala/docs/http/scaladsl/HttpClientExampleSpec.scala]

Miscellaneous:
* [https://pspdfkit.com/blog/2018/how-to-use-docker-compose-to-run-multiple-instances-of-a-service-in-development/]
* [https://stackoverflow.com/questions/630453/put-vs-post-in-rest:]
* [https://medium.com/@apiltamang/unicode-utf-8-and-ascii-encodings-made-easy-5bfbe3a1c45a]
