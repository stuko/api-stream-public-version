#!/bin/bash

BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
source ${BASEDIR}/set-api-env.sh
echo "ARG_OPT is ${ARG_OPT}"
echo "ENV_HOST is ${ENV_HOST}"
echo "ENV_OPT is ${ENV_OPT}"
echo "LOGGER_PORT is ${LOGGER_PORT}"

cd api-collector-logger
echo "start , docker build of logger"
./gradlew build
echo "gradle build success....."
docker build --tag ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-logger-docker .
echo "success , docker build of logger"
docker stop api-logger
docker rm api-logger
docker run -d --name api-logger -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT}  ${DOCKER_OPTIONS} --restart always -p ${LOGGER_PORT}:${LOGGER_PORT} -e PROFILES=dev ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-logger-docker
cd ..
