#!/bin/bash

BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
source ${BASEDIR}/set-api-env.sh
echo "ARG_OPT is ${ARG_OPT}"

cd api-collector-stream
echo "start , docker build of stream"   
docker build  ${STREAM_ARG} --tag ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-stream-docker .
echo "success , docker build of stream"
docker stop api-stream
docker rm api-stream
# docker run -d --name api-stream  ${DOCKER_OPTIONS} --mount type=bind,source=/tmp,target="$(pwd)"/volume/storm/tmp -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT} --restart always -p ${STREAM_PORT}:${STREAM_PORT} -p ${HTTP_PORT}:${HTTP_PORT} -e STREAM_LOG_DEBUG=true -e MIN=512m -e MAX=1024m ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-stream-docker
# docker run -d --name api-stream  ${DOCKER_OPTIONS} --mount type=bind,source=${BASEDIR}/volume/storm/tmp,target=/tmp -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT} --restart always -p ${STREAM_PORT}:${STREAM_PORT} -p ${HTTP_PORT}:${HTTP_PORT} -e STREAM_LOG_DEBUG=true -e MIN=512m -e MAX=1024m ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-stream-docker
docker run -d --name api-stream  ${DOCKER_OPTIONS} -e "TZ=Asia/Seoul" ${ENV_HOST} ${ENV_OPT} --restart always -p ${STREAM_PORT}:${STREAM_PORT} -p ${HTTP_PORT}:${HTTP_PORT} -e STREAM_LOG_DEBUG=true -e MIN=512m -e MAX=1024m ${THIS_IP}:${DOCKERHUB_PORT}/api/api-collector-stream-docker
cd ..
