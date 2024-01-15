#!/bin/bash

echo "If you want to install all , Your environment need to execute Linux Shell"
echo "You need to install Kafka, This system does not include the Kafka"
BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
echo "BASE is ${API_HOME}"
source ${API_HOME}/set-env.sh

docker stop api-backend
docker rm api-backend
