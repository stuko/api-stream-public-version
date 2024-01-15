#!/bin/bash

BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
source ${BASEDIR}/set-api-env.sh
echo "ARG_OPT is ${ARG_OPT}"

cd api-collector-scheduler
echo "start , docker build of scheduler"
./gradlew build
docker build ${SCHEDULER_ARG} --tag ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-scheduler-docker .
echo "success , docker build of scheduler"
docker stop api-scheduler
docker rm api-scheduler
docker run -d --name api-scheduler -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT}  ${DOCKER_OPTIONS} --restart always -p ${SCHEDULER_PORT}:${SCHEDULER_PORT} -e PROFILES=dev ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-scheduler-docker
cd ..

