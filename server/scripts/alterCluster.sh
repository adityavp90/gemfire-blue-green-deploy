#!/bin/bash

gfsh <<EOF
connect --locator=localhost[41111]

deploy --jar=../../functions/target/functions-1.0-SNAPSHOT.jar

EOF
