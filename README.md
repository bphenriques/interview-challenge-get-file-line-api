# Salsify Line Server

Server that serves a single immutable file  through a Rest API.

## How it works?

This system was built to scale both horizontally and vertically. There two main components:
* `client`: 
* `shard`:

## How to run

To run the application use the following command in the application's folder:

```bash
$ bin/lineserver <path-to-file>
```

Replace `<path-to-file>` with the path to a file in the following [format](#file-format).

### Configuration

The simulation configuration is defined in the `conf/application.conf` file:
 
```
FIXME
```
 
Where:

## Scalability

The system is designed to scale both horizontally or vertically:
* One may scale horizontally in number of shards to accommodate larger files. The number of shards depends on the 
  available memory in each shard.
* One may add more clients to the load balancer to accommodate greater number of clients. Moreover, one can tweak the
  cache to accommodate both business patterns and memory restrictions in order to have balanced cachegreater hit ratio.


## External libraries

* 

* How does your system work? (if not addressed in comments in source)
* How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?
* How will your system perform with 100 users? 10000 users? 1000000 users?
* What documentation, websites, papers, etc did you consult in doing this assignment?
* What third-party libraries or other tools does the system use? How did you choose each library or framework you used?
* How long did you spend on this exercise? If you had unlimited more time to spend on this, how would you spend it and how would you prioritize each item?
* If you were to critique your code, what would you have to say about it?

