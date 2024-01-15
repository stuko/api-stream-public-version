#!/bin/bash

BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
source ${BASEDIR}/set-api-env.sh
echo "ARG_OPT is ${ARG_OPT}"

${API_HOME}/install_api_backend.sh
${API_HOME}/install_api_scheduler.sh
${API_HOME}/install_api_stream.sh
${API_HOME}/install_api_logger.sh

#docker service rm api-stream
#docker service create --name api-stream --replicas 3 --publish target=8120,published=8120 -e PROFILES=dev api/api-collector-stream-docker
#./install_swarm_api_stream.sh
