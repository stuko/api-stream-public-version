#!/bin/bash

echo "If you want to install all , Your environment need to execute Linux Shell"
echo "You need to install Kafka, This system does not include the Kafka"
BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
echo "BASE is ${API_HOME}"
source ${API_HOME}/set-env.sh

${API_HOME}/shutdown_nginx.sh

${API_HOME}/shutdown_mongo.sh

${API_HOME}/shutdown_postgres.sh

${API_HOME}/shutdown_api_stream.sh

${API_HOME}/shutdown_api_logger.sh

${API_HOME}/shutdown_api_scheduler.sh

${API_HOME}/shutdown_api_backend.sh

${API_HOME}/shutdown_api_elk.sh

${API_HOME}/shutdown_api_kafka.sh

${API_HOME}/install_api_restart_container.sh

