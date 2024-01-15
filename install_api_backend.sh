#!/bin/bash

BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
source ${BASEDIR}/set-api-env.sh
echo "ARG_OPT is ${ARG_OPT}"
echo "ENV_HOST is ${ENV_HOST}"
echo "ENV_OPT is ${ENV_OPT}"
echo "BACKEND_PORT is ${BACKEND_PORT}"
echo "BACKEND_ARG is ${BACKEND_ARG}"

cd api-collector-backend-kotlin
echo "start , docker build of backend"
./gradlew build
echo "gradle build success....."
docker build ${BACKEND_ARG} --tag ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-backend-kotlin-docker .
echo "success , docker build of backend"
docker stop api-backend
docker rm api-backend
docker run -d --name api-backend -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT}  ${DOCKER_OPTIONS} --restart always -p ${BACKEND_PORT}:${BACKEND_PORT} -e PROFILES=dev ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-backend-kotlin-docker
cd ..
