<p align="center">
  <img src="https://github.com/bphenriques-lab/get-file-line-api-challenge/actions/workflows/build.yaml/badge.svg" />
  <img alt="Scala" src="https://img.shields.io/static/v1?style=flat-square&logo=Scala&label=&message=Scala&color=gray">
  <img alt="Docker" src="https://img.shields.io/static/v1?style=flat-square&logo=Docker&label=&message=Docker&color=gray">
</p>

Project made in 2017 with a minor review in 2021.

Some thoughts in 2021 and I may address because I had fun in this project:
- Organize a bit the project to have the project structure reflect a bit the deployment structure.
- Review tests.
- Review some parts now that I know a bit more about Akka.
- Today I frown upon the way the Docker image is being built as I seem to lose control. I might revisit.
- Explore a more simplistic approach which a mere single server reading from a local file and, to optimize
  accesses, have a map from line to a range of memory in the file (sort of speech... need to R&D this...). Not as performant
  but it might be good enough.
- Many other things, for now. Just reviewed a bit the main code. Not going to bump the project.

---
# Specification

Your system should act as a network server that serves individual lines of an immutable text file over the network to clients using the following simple REST API:

`GET /lines/<line index>`
* Returns an HTTP status of 200 and the text of the requested line or an HTTP 413 status if the requested line is beyond the end of the file.

Your server should support multiple simultaneous clients.

The system should perform well for small and large files.

The system should perform well as the number of GET requests per unit time increases.

You may pre-process the text file in any way that you wish so long as the server behaves correctly.

The text file will have the following properties:
* Each line is terminated with a newline ("\n").
* Any given line will fit into memory.
* The line is valid ASCII (e.g. not Unicode).

# Solution

This system has two main components:
* `client`: Responsible to handle external line requests.
* `shard`: Scalable key-value store.

Given that the system must scale to support files as big as 100GB and handle 1000000 clients, it was built to scale both
vertically and horizontally. Vertically as we can always give more resources to the machine to handle more requests and/or
bigger files, and horizontally as vertically can only scale up to a certain point. As detailed in the following sections,
it is possible to easily add more nodes to the system to accommodate ambitious business requirements.
 
### Client

The `client` is the _front_ facing REST API server that manages external requests.

#### Endpoints:

It has two endpoints:
* `GET /lines/<line index>`: Returns an HTTP status of 200 and the text of the requested line or an HTTP 413 status if 
   the requested line is beyond the end of the file. `<line index>` is an Integer.
* `GET /health`: Returns HTTP 200. 
    
#### Sharding

The `client` connects throughout an _existing_ pool of `shards` through HTTP to store and obtain the lines given its 
location in the original file. If a file is specified in the `application.conf` under `client.input`, the client is 
considered `primary` and will be responsible for distributing the file throughout the shards in round-robin. Otherwise,
it is considered `secondary` with the role of offsetting part of the load from the `primary`. 

Given that the number of shards *is fixed*, we can easily obtain the location of a line in the cluster *without having 
to store its location*:

    shard(lineIndex) = lineIndex % numberOfShards

Similar to Redis's `resharding`, dynamically adding more shards would require to reindex the file so that the lines are 
evenly distributed throughout the new shards topology.

#### Performance and Scalability

To reduce the number of network requests between the `client` and the `shards`, two optimizations are in-place:
* `client` knows the number of lines being served to quickly reject lines out-of-range without having to contact the `shards`.
* `client` has a [in-memory LFU cache](https://doc.akka.io/docs/akka-http/current/common/caching.html) that stores the
  most frequent *responses*, which avoids expensive network requests to the `shards` pool. This cache can be as large
  as the machine's CPU allows and the client's usage pattern justifies it (e.g., a cache in a purely random usage pattern,
  has very limited benefits).

Moreover, as mentioned, to offset part of the load from the `primary`, additional `secondary` clients can be added. This 
allows the server to handle more concurrent requests and have additional caches.

### Shard

The `shard` is responsible for storing key-value pairs, in this project the key is an `Integer` and the value a `String`.

#### Endpoints:

It has three endpoints:
* `GET /key/<key>`: Returns an HTTP status of 200 and the String value stored and Integer index `<key>`.
* `PUT /key/<key> <body>`: Sets the content of the `<body>` as the value of `<key>` and returns HTTP 201.
* `GET /health`: Returns HTTP 200. 

#### Performance and Scalability

Inspired by the existing key-value stores such as Redis, each shard stores in-memory a map with the lines assigned to 
it. which is much faster than disk access.

Moreover, given that each shard can live on its own, we can add more `shards` to handle bigger files without loss of
performance. Lastly, and not the least, the shards do not need to have access to the original file being served however,
the start-up (one-time operation) time is lengthier as the file is distributed throughout the shards. 


## How to run

To run the Line Server, you will need at least one `client` and one `shard`. We recommend using a Docker orchestration
tool to launch as many clients and shards as you see fit, using the following docker image `lineserver:latest`. You will
find an example in the `docker` folder on how to accomplish this using docker-compose.

### Configuration

The simulation configuration is defined in the `conf/application.conf` file which is transcribed below:
 
```
# The node type. Either 'client' or 'shard'
node-type = "client"

# The client' configuration.
client {

  # The binding host and port.
  http {
    host = "0.0.0.0"
    port = 8080
  }

  # The input. Omit if the client is secondary.
  input {
    type = "local-file"
    local-file {
      path = "/opt/file.txt"
    }
  }

  # The shards management configuration.
  manager {
    type = "shards-round-robin"
    shards-round-robin {
      shards: [
        {
          host = "http://shard1"
          port = 8080
          requests-queue-size = 500
        },
        {
          host = "http://shard2"
          port = 8080
          requests-queue-size = 500
        }
      ]
    }
  }
}

# The shards configuration.
shard {

  # The binding host and port.
  http {
    host = "0.0.0.0"
    port = 8080
  }
}

// See https://doc.akka.io/docs/akka-http/current/configuration.html.
akka {
  loglevel = INFO

  http {
    caching {
      lfu-cache {
        # Maximum number of entries the cache may store. Same as Akka default.
        max-capacity = 2048

        # Minimum total size for the internal data structures. Same as Akka default.
        initial-capacity = 512

        # Upper limit to the time period an entry is allowed to remain in the cache. Same as Akka default.
        time-to-live = infinite

        # Maximum time period an entry is allowed to remain in the cache after last access. Same as Akka default.
        time-to-idle = infinite
      }
    }
  }
}
```

Note that, there is no timeout eviction policy in the cache by default. This is intentional as there no updates,
therefore we can store as many entries in the cache as the size allows it. 

## Limitations

* Only supports files up to `Int.MaxValue` (2^31 - 1).
* Does not support HTTPS.

