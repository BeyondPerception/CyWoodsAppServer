#!/bin/bash

# This script deploys the new server to the testing enviornment

REMOTE="dentHome"

echo "Entering maven project: CyWoodsAppServer"
cd CyWoodsAppServer

echo "Starting maven build process"
mvn clean package
if [[ "$?" -ne 0 ]] ; then
  echo 'Failed to build maven project'
  echo "Exiting..."
  exit $rc
fi

echo "Entering target directory"
cd target

echo "Cleaning tomcat webapps directory"
password=$(cat ~/.ssh/password)
expect -c 'spawn ssh '$REMOTE' "rm -rf apache-tomcat-9.0.21/webapps/*"; expect "*:"; send "'$password'\r"; expect eof'

echo "Starting file transfer to dentHome"
expect -c 'spawn scp CyWoodsAppServer.war '$REMOTE':apache-tomcat-9.0.21/webapps; expect "*:"; send "'$password'\r"; expect eof'
echo "Completed file transfer"

echo "Restarting Server"
expect -c 'spawn ssh '$REMOTE' "./apache-tomcat-9.0.21/bin/startup.sh"; expect "*:"; send "'$password'\r"; expect eof'
