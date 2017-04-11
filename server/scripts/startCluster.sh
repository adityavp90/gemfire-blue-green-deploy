#!/bin/bash

gfsh <<EOF
start locator --name=locator1 --initial-heap=100m --max-heap=100m --log-level=warning --cache-xml-file=../src/main/config/serverCache.xml --locators=localhost[41111] --mcast-port=0 --bind-address=127.0.0.1 --port=41111

start server --name=server1 --server-port=0 --initial-heap=100m --max-heap=100m --start-rest-api=true --http-service-port=28080 --http-service-bind-address=localhost --log-level=warning --cache-xml-file=../src/main/config/serverCache.xml --locators=localhost[41111] --mcast-port=0 --bind-address=127.0.0.1

#connect --locator=localhost[41111]
#deploy --jar=../../functions/target/functions-1.0-SNAPSHOT.jar

#alter region --name=/Person --cache-writer=io.pivotal.gemfire.sample.cachewriter.AttributeSyncCacheWriter
#change loglevel --loglevel=fine --members=server1

EOF
