#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
docker-compose -f ./api-kafka/docker-compose.yml down
docker-compose -f ./api-kafka/docker-compose.yml up -d
