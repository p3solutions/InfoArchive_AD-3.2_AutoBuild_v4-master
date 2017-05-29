#!/bin/sh
export ANT_HOME
export ANT_OPTS
export CLASSPATH

ANT_HOME=$DDS_DIR/apache-ant
ANT_OPTS='-Xmx512m -XX:MaxPermSize=128M'
CLASSPATH=
  
$DDS_DIR/apache-ant/bin/ant "$@"

