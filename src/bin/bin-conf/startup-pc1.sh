#!/bin/sh
rm -f ../logs/*
export OPENFIRE_OPTS="-Xms256m -Xmx512m"
nohup ./openfire.sh >/dev/null&