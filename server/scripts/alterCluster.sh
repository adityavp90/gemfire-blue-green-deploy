#!/bin/bash

gfsh <<EOF
connect --locator=localhost[41111]

deploy --jar=../../functions/target/functions-1.0-SNAPSHOT.jar

create async-event-queue --id=SynchObjectVersions --batch-size=1  --listener=io.pivotal.gemfire.sample.asyncheventqueue.AttributeAsyncEventListener

alter region --name=/Person ---async-event-queue-id=SynchObjectVersions

EOF
