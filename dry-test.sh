#!/usr/bin/env bash

echo "Insert 1:Blue and 2:Green ..."
curl -XPUT "localhost:9080/key/1" -d'Blue'
curl -XPUT "localhost:9081/key/2" -d'Green'

echo ""

echo "Blue? ..."
curl -XGET "localhost:8080/lines/1"

echo ""

echo "Green? ..."
curl -XGET "localhost:8080/lines/2"
