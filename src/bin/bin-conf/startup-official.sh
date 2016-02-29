#!/bin/sh
rm -f ../logs/*
chmod a+x /root/bin/openfire/plugins/vplugin/web/docs/tasks/autobak.sh
export OPENFIRE_OPTS="-Xms8192m -Xmx10240m"
nohup ./openfire.sh >/dev/null&