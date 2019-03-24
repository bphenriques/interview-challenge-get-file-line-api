#!/usr/bin/env bash
# A script that takes a single command-line parameter which is the name of the file to serve.

# Full path to the file you wish to serve.
export FILE="$1"
make start

