#!/bin/bash

gfsh <<EOF

connect --locator=localhost[41111]
stop server --name=server1
stop locator --name=locator1

EOF

rm -rf locator1 server1
