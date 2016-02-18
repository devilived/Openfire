#!/bin/sh
export OPENFIRE_OPTS="-Xms256m -Xmx512m"
nohup ./openfire.sh >/dev/null&