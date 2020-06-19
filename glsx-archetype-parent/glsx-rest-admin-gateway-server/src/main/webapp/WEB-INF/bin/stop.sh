#!/bin/bash

####################################
#                                  #
#    General shell v1.0.0.1        #
#                                  #
#                                  #
####################################

SERVER_NAME="glsx-rest-admin-gateway-server"
PID_DIR="/app/soft/java_project/pids"

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
if [ -d "classes" ];then
   	CONF_DIR="$(pwd)/classes"
elif [ -d "conf" ];then
    	CONF_DIR="$(pwd)/conf"
fi

#if [ -f "$PID_DIR/${SERVER_NAME}.pid" ];then
#	PID=`cat $PID_DIR/${SERVER_NAME}.pid`
#else
PID=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" |awk '{print $2}'`
#fi

if [ -z "$PID" ];then
	echo "The program is not running"
	exit 1
fi

pcount=`ps --no-heading -p $PID | wc -l`
if [ "$pcount" -ne 1 ];then
    echo "ERROR: $SERVER_NAME does not started!"
    exit 1
fi


echo -e "Stopping $SERVER_NAME ...\c"

kill $PID > /dev/null 2>&1

COUNT=0
ctime=`date +"%s"`
while [ $COUNT -lt 1 ]; do 
    echo -e ".\c"
    sleep 1
    COUNT=1
    PID_EXIST=`ps --no-heading -p $PID`
    if [ -n "$PID_EXIST" ]; then
        ctime2=`date +"%s"`
        dtime=$((ctime2 - ctime))
        if [ "$dtime" -ge 10 ];then
            echo -e "\nstop program timeout, use kill -9 $PID"
            kill -9 $PID >/dev/null 2>&1
            if [ ! $? -eq 0 ];then
                echo "No permissions"
                exit 2
            else
                rm -rf $PID_DIR/${SERVER_NAME}.pid 
            fi
        fi
        COUNT=0
        continue
    fi
done

echo "OK!"
rm -rf $PID_DIR/${SERVER_NAME}.pid