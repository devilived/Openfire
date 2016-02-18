#!/bin/sh
export OPENFIRE_OPTS="-Xms8192m -Xmx10240m"
nohup ./openfire.sh >/dev/null&