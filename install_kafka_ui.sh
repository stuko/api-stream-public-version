#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"

docker stop kafka-ui
docker rm kafka-ui

docker run -p 8083:8080 \
        --name kafka-ui \
	-e KAFKA_CLUSTERS_0_NAME=local \
	-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=${KAFKA_IP}:9092 \
	-d provectuslabs/kafka-ui:latest
