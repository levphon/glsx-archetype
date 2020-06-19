#!/bin/bash

####################################
#                                  #
#    General shell v1.0.0.1        #
#                                  #
#                                  #
####################################


###########init variable#############
JAVA_HOME="/app/soft/jdk1.8.0_144"
SERVER_PORT="8820"
DUBBO_PORT=""
JMX_ARG="-Xmx1024m -Xms512m -Xmn256m"
MAIN="cn.com.glsx.Application"
SERVER_NAME="glsx-rest-wechat-admin"

###########end#######################

PID_DIR="/app/soft/java_project/pids"

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
if [ -d "classes" ];then
    CONF_DIR="$(pwd)/classes"
    LIB_DIR="$(pwd)/lib"
elif [ -d "conf" ];then
    CONF_DIR="$(pwd)/conf"
    LIB_DIR="$(pwd)/lib"
fi

LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
export CLASSPATH=$CONF_DIR:$LIB_JARS

if [ -z "$CONF_DIR" ];then
    echo "Configure dir not found"
    exit 1
fi

PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The app is already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ ! -d "$PID_DIR" ];then
    mkdir -p $PID_DIR
fi

CHECK_PORT=$2
if [ -n "$SERVER_PORT" ];then
     SERVER_PORT_COUNT=`netstat -tln | grep -w $SERVER_PORT | wc -l`
     if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The port $SERVER_PORT is already used!"
        exit 1
     fi
fi

if [ -n "$DUBBO_PORT" ];then
     DUBBO_PORT_COUNT=`netstat -tln | grep -w $DUBBO_PORT | wc -l`
     if [ $DUBBO_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The port $DUBBO_PORT already used!"
        exit 1
     fi

fi

if [[ -z "$SERVER_PORT" && -z "$DUBBO_PORT" ]];then
    CHECK_PORT="skip"
fi

LOG_FILE="/data/java_log/jetty/${SERVER_NAME}/${SERVER_NAME}.log"
LOG_DIR=`dirname $LOG_FILE`
if [ ! -d $LOG_DIR ];then
    mkdir -p $LOG_DIR
fi

############java args #######


#JAVA_OPTS="$PINPOINT -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Ddubbo.registry.file=/home/jdkapp/.dubbo/${SERVER_NAME}.cache"
JAVA_OPTS="$PINPOINT -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"
#JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"

JAVA_DEBUG_OPTS=""
if [ "$2" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

JAVA_JMX_OPTS=""
if [ "$2" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi


BITS=`file $JAVA_HOME/bin/java | grep 64-bit`
if [ -n "$BITS" ]; then
        if [ -n "$JMX_ARG" ];then
            JAVA_MEM_OPTS="-server $JMX_ARG -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
        else
            JAVA_MEM_OPTS=" -server -Xmx1g -Xms1g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
        fi
else
        JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echo -e "Starting $SERVER_NAME \c"

nohup $JAVA_HOME/bin/java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -DCLASS_DIR=$CONF_DIR:$LIB_DIR $MAIN  > /dev/null 2>&1 &
PIDS=$!

if [ "$CHECK_PORT" != "skip" ];then
   COUNT=0
   ERROR=0
   TIME_OUT=60
   ctime=`date +"%s"`
   while [ $COUNT -lt 1 ]; do 
        ctime2=`date +"%s"`
        echo -e ".\c"
        sleep 1 
        #PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" | awk '{print $2}'`
        RESULT=`ps  --no-heading -p $PIDS`
        if [ -n "$RESULT" ];then
            if [ -n "$DUBBO_PORT" ]; then
                #COUNT=`echo status | nc 127.0.0.1 $DUBBO_PORT -i 1 | grep -c OK`
                COUNT=`/usr/sbin/ss -lpn | grep "${DUBBO_PORT}.*$PIDS" | wc -l`
            else
                   if [ -n "$SERVER_PORT" ]; then
                       #COUNT=`netstat -ntl | grep -w $SERVER_PORT | wc -l`
                       COUNT=`/usr/sbin/ss -lpn | grep "${SERVER_PORT}.*$PIDS" | wc -l`
                   fi
            fi
                        
            if [ $COUNT -gt 0 ]; then
		echo $PIDS > $PID_DIR/${SERVER_NAME}.pid 
                break
            fi
            
            dtime=$((ctime2 - ctime))
            if [ "$dtime" -ge "$TIME_OUT" ];then
		echo $PIDS > $PID_DIR/${SERVER_NAME}.pid 
                echo -e "\ncheck port status timeout"
                exit 1
            fi

        else
            ERROR=$((ERROR + 1))
            if [ "$ERROR" -gt 3 ];then
                echo -e "\nProgram startup failed"
                exit 2
            fi
        fi
    done
fi
#服务检测与注册
if [[ "x$SERVER_PORT" != "x" && -f ./bin/check.sh ]]
then
    ./bin/check.sh $SERVER_NAME $SERVER_PORT $PIDS &
fi
echo "PID: $PIDS"
echo "LOGFILE: $LOG_FILE"