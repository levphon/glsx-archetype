#!/bin/bash

cd `dirname $0`
./stop.sh
if [ ! $? -eq 2 ];then
    ./start.sh
fi