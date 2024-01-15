#!/bin/bash

echo "If you want to install all , Your environment need to execute Linux Shell"
echo "You need to install Kafka, This system does not include the Kafka"
BASEDIR=$(pwd)
export API_HOME=${BASEDIR}
echo "BASE is ${API_HOME}"
source ${API_HOME}/set-env.sh

input=$1
if [ $input -n ]
then
  ${API_HOME}/install_nginx_bg.sh
  ${API_HOME}/install_postgres.sh
  ${API_HOME}/install_mongo.sh
  ${API_HOME}/install_api_kafka.sh
  ${API_HOME}/install_api.sh
  ${API_HOME}/install_front.sh
  ${API_HOME}/install_api_restart_container.sh
  # ${API_HOME}/install_api_elk.sh
elif [ $input = "front" ]
then
  ${API_HOME}/install_front.sh
elif [ $input = "nginx" ]
then
  ${API_HOME}/install_nginx_bg.sh
elif [ $input = "postgre" ]
then
  ${API_HOME}/install_postgres.sh
elif [ $input = "mongo" ]
then
  ${API_HOME}/install_mongo.sh
elif [ $input = "api" ]
then
  ${API_HOME}/install_api.sh
elif [ $input = "elk" ]
then
  ${API_HOME}/install_api_elk.sh
elif [ $input = "kafka" ]
then
  ${API_HOME}/install_api_kafka.sh
else
  echo "./install_all.sh or ./install_all.sh [front/nginx/postgre/mongo/api]"
fi
