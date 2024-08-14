#!/bin/bash

JAVA_HEAP_OPTS="-server -Xms512m -Xmx512m -Xss256k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
JAVA_MEM_OPTS=" ${JAVA_HEAP_OPTS}
 -XX:SurvivorRatio=8
 -XX:-UseAdaptiveSizePolicy
 -XX:-UseGCOverheadLimit
 -XX:+DisableExplicitGC
 -XX:+UseConcMarkSweepGC
 -XX:+CMSParallelRemarkEnabled
 -XX:+UseCMSCompactAtFullCollection
 -XX:+UseFastAccessorMethods
 -XX:+UseCMSInitiatingOccupancyOnly
 -XX:CMSInitiatingOccupancyFraction=70
 -XX:LargePageSizeInBytes=128m
 "
JAVA_DUMP_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs/dump"


nohup java ${JAVA_MEM_OPTS}  ${JAVA_DUMP_OPTS}  ${JAVA_OPTS} -jar ../datasync.jar --syn.config.file=/data/bd/synConfig.json --spring.profiles.active=test --spring.config.additional-location=../config/application-test.yml 2>&1 > stdout.log &


#--spring.config.location=application-test.yml