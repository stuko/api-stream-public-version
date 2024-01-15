#!/bin/bash

MY_LOCAL_IP=`hostname -I | cut -d' ' -f1`

if [[ "$OSTYPE" == "msys"* ]]; then
  MY_LOCAL_IP=`netstat -rn | grep "0.0.0.0" | head -1 | awk '{print $4}'`
fi
# MY_LOCAL_IP=`netstat -rn | grep "0.0.0.0" | head -1 | awk '{print $4}'`
# MY_LOCAL_IP=`ifconfig | grep -G "192.168.*.255" | cut -d' ' -f10`

export DOCKER_OPTIONS='--log-opt max-size=10m --log-opt max-file=3'

echo "########################################"
echo "Check mandatory !!!!!!"
echo "1. check forlder exist ? : ./volume/mongo/data/"
echo "2. check forlder exist ? : ./volume/postgres/data/pgdata/"
echo "3. check forlder exist ? : ./volume/nginx/log/"
echo "4. check forlder exist ? : ./volume/storm/tmp/"
echo "5. check gradlew is execuatable ?"
echo "6. check local registry is installed.. 5000 port ?"
echo "7. check use by sudo?"
echo "8. check react-script is installed?"
echo "9. if you install ELK with Monstache, check ./volume/elasticsearch/data ?"
echo "10. if you install ELK with Monstache, check ./volume/monstache/config/monstache.config.toml ?"
echo "########################################"

export IP=$MY_LOCAL_IP

if [ -z "$IP" ]; then
  echo "Is this your IP? : ${MY_LOCAL_IP} [y/n]"
  read REPLY
  YN=$REPLY
  if [ $YN = 'n' ]; then
    echo "Hello, please input your IP address"
    echo -n "Your IP address: "
    read REPLY
    export IP=$REPLY
    echo "your IP address is ${IP}"
  else
    export IP=$MY_LOCAL_IP
  fi
fi
echo "IP address : ${IP} will be used."

export THIS_IP=${IP}
export LOCAL_IP=${IP}

export ZOOKEEPER_IP=${THIS_IP}
export ZOOKEEPER1_IP=${THIS_IP}
export ZOOKEEPER2_IP=${THIS_IP}
export ZOOKEEPER3_IP=${THIS_IP}
export ZOOKEEPER_PORT="2181"
export KAFKA_IP=${LOCAL_IP}
export KAFKA1_IP=${LOCAL_IP}
export KAFKA2_IP=${LOCAL_IP}
export KAFKA3_IP=${LOCAL_IP}
export MONGO_IP=${THIS_IP}
export MONGO1_IP=${THIS_IP}
export MONGO2_IP=${THIS_IP}
export MONGO3_IP=${THIS_IP}
export ELASTIC_IP=${THIS_IP}
export NGINX_IP=${THIS_IP}
export POSTGRE_IP=${THIS_IP}
export KAFKA_PORT="9092"
export MONGO_PORT="27017"
export NGINX_PORT="8090"
export POSTGRE_PORT="5432"
export HTTP_PORT="9999"
export STREAM_IP=${THIS_IP}
export LOGGER_IP=${THIS_IP}
export SCHEDULER_IP=${THIS_IP}
export BACKEND_IP=${THIS_IP}
export STREAM_PORT="8120"
export LOGGER_PORT="8140"
export SCHEDULER_PORT="8130"
export BACKEND_PORT="8110"
export DOCKERHUB_PORT="5000"

export TOPOLOGY_GROUP="TopologyGroup1,TopologyGroup2,TopologyGroup3"
export STREAM_SHELL_DIR="/docker/shell"
export STREAM_SHELL_START="./install_swarm_api_stream.sh &"
export STREAM_STORM_LOCAL_DIR="./volume/storm/tmp"

chmod a+x **/gradlew

if [ ! -d './volume/monstache/config/' ]; then mkdir -p ./volume/mongo/data/ ; fi
if [ ! -d './volume/postgres/data/pgdata/' ]; then mkdir -p ./volume/postgres/data/pgdata/ ; fi
if [ ! -d './volume/nginx/log/' ]; then mkdir -p ./volume/nginx/log/ ; fi
if [ ! -d './volume/storm/tmp/' ]; then mkdir -p ./volume/storm/tmp/ ; fi
if [ ! -d './volume/elasticsearch/data/' ]; then mkdir -p ./volume/elasticsearch/data/ ; fi
if [ ! -d './volume/monstache/config/' ]; then mkdir -p ./volume/monstache/config/ ; fi
