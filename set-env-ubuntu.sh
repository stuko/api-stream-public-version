#!/bin/bash

export THIS_IP="192.168.0.17"
export LOCAL_IP="192.168.0.17"

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
export NGINX_IP=${THIS_IP}
export POSTGRE_IP=${THIS_IP}
export KAFKA_PORT="9092"
export MONGO_PORT="27017"
export NGINX_PORT="8090"
export POSTGRE_PORT="5432"
export STREAM_IP=${THIS_IP}
export SCHEDULER_IP=${THIS_IP}
export BACKEND_IP=${THIS_IP}
export STREAM_PORT="8120"
export SCHEDULER_PORT="8130"
export BACKEND_PORT="8110"

export TOPOLOGY_GROUP="TopologyGroup1,TopologyGroup2,TopologyGroup3"
export STREAM_SHELL_DIR="/sw/dsapp/docker/shell"
export STREAM_SHELL_START="./install_swarm_api_stream.sh &"
export STREAM_STORM_LOCAL_DIR="./volume/storm/tmp"

echo "########################################"
echo "Check mandatory !!!!!!"
echo "check forlder exist ? : ./volume/mongo/data/"
echo "check forlder exist ? : ./volume/postgres/data/pgdata/"
echo "check forlder exist ? : ./volume/nginx/log/"
echo "check forlder exist ? : ./volume/storm/tmp/"
echo "########################################"

if [ -z "$IP" ]; then
  echo "Is this your IP? : ${THIS_IP} [y/n]"
  read -r
  YN=$REPLY
  if [ $YN = 'n' ]; then
    echo "Hello, please input your IP address"
    echo -n "Your IP address: "
    read -r
    export IP=$REPLY
    echo "your IP address is ${IP}"  
  fi
fi


