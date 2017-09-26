#!/bin/bash

PROP=./WEB-INF/classes/network.properties

# step #1: configure the network.propoerties file
mkdir -p ./WEB-INF/classes/
touch $PROP
echo "databaseHostName=$db_host" >> $PROP
echo "databasePort=$db_port" >> $PROP

# step #2: update the webapp to load the right properties
jar uvf ./webapps/tcs-service-document.war $PROP

# step #3: start the TomEE engine
catalina.sh run
