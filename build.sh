#!/usr/bin/env bash

# Builds the system. This will build the docker images. See README.md for more information.
sbt docker:publishLocal
