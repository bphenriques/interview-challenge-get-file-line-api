# Light Technical Design Document

## Requirements

* System that serves individual lines of an immutable text file.
* The server should provide a single endpoint:
    
    ```
    GET /lines/<line index>
    ```
    
    For a given <line index> (i.e., number of hte line), returns HTTP status of 200 and the text of the requested line 
    or an HTTP 413 status if the requested line is beyond the end of the file.

* The server should support multiple simultaneous clients.
* The system should perform well for small and large files.
* The system should perform well as the number of GET requests per unit time increases.

## Assumptions

The text file will have the following properties:
* Each line is terminated with a newline ("\n").
* Any given line will fit into memory.
* The line is valid ASCII (e.g. not Unicode).


## Open Questions

* Does the build system has SBT, Scala and Java configured?

* This is personal preference: Can it be a `README.md` file instead of `README`? I prefer the files with extensions as
  they give more information about the file.
  
* What are the SLAs for each client tier?

* What is the usage pattern?

# Dump

Worklog: March 8th 17:00 until 18:00: Preparing project, reading requirements and reading on the subject.


Given the requirements clearly state that the server has to handle efficiently (how efficient is other question) both
large files and a great number of simultaneous clients, we are leaning towards a distributed system b/c a single server
can only handle so much, specially if it requires making available a large 1000GB file through the network to 100000
clients in parallel.

The simplest solution would be store the file in a database.


At first thought, one things of storing the file a database, however 

From my experience, I

 
Given the requirements, in order to have 

Investigated if anyone has already worked on this subject.


 

