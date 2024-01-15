#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"

docker-compose -f kafka-ui-compose.yml down
docker-compose -f kafka-ui-compose.yml up -d kafka-ui
