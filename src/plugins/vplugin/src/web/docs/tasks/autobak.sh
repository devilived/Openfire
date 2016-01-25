#!/bin/sh
BAK_HOME=/alidata1/data/bak
rq=`date +%Y%m%d%H%M%S`


mysqldump -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com --extended-insert=false tel v_log > $BAK_HOME/v_log-$rq.sql
mysql     -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com -e "TRUNCATE TABLE tel.v_log"

mysqldump -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com --extended-insert=false tel v_order > $BAK_HOME/v_order-$rq.sql
mysql     -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com -e "TRUNCATE TABLE tel.v_order"

mysqldump -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com --extended-insert=false tel v_paylog > $BAK_HOME/v_paylog-$rq.sql
mysql     -utelephone -ptelephone_123 -htelephone.mysql.rds.aliyuncs.com -e "TRUNCATE TABLE tel.v_paylog"

cd $BAK_HOME 
tar -czf baksql-$rq.tgz *.sql
rm *.sql
