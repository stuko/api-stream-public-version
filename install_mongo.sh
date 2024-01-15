#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
docker pull mongo
docker stop mongo-api
docker rm mongo-api
if [ ! -d './volume/mongo/data' ]; then
  mkdir -p ./volume/mongo/data
fi;

docker run --name mongo-api -e "TZ=Asia/Seoul" -p ${MONGO_PORT}:${MONGO_PORT} \
--mount type=bind,source=${BASEDIR}/volume/mongo/data,target=/data/db  ${DOCKER_OPTIONS} \
-d  --restart always mongo:4.4.10
# -v ${BASEDIR}/volume/mongo/data:/data/db -d mongo 
 # --config /etc/mongod.conf
 # -v ${BASEDIR}/volume/mongo/conf/mongod.conf:/etc/mongod.conf \
