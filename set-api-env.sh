#!/bin/bash

export ENV_HOST="--add-host=kafka1:${KAFKA1_IP} --add-host=kafka2:${KAFKA2_IP} --add-host=kafka3:${KAFKA3_IP} --add-host=mongo1:${MONGO1_IP} --add-host=mongo2:${MONGO2_IP} --add-host=mongo3:${MONGO3_IP} --add-host=kafka:${KAFKA_IP} --add-host=zookeeper:${ZOOKEEPER_IP} --add-host=mongo:${MONGO_IP} --add-host=postgre:${POSTGRE_IP} --add-host=backend:${BACKEND_IP} --add-host=scheduler:${SCHEDULER_IP}"
export ENV_OPT="-e spring.kafka.bootstrap-servers=kafka:${KAFKA_PORT} -e api.collector.mongo.url=mongo1:${MONGO_PORT} -e api.collector.kafka.broker=${KAFKA_IP} -e api.collector.kafka.port=${KAFKA_PORT} -e api.collector.kafka.bootstrap=kafka:${KAFKA_PORT} -e api.collector.mongo.url=mongo1:${MONGO_PORT} -e spring.datasource.data.hikari.url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres -e spring.datasource.url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres -e spring.datasource.jdbc-url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres -e spring.db.datasource.url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres"
export ARG_OPT=" \
--build-arg spring_kafka_bootstrap_servers=kafka:${KAFKA_PORT} \
--build-arg api_collector_mongo_url=mongo1:${MONGO_PORT} \
--build-arg api_collector_kafka_broker=${KAFKA_IP} \
--build-arg api_collector_kafka_port=${KAFKA_PORT} \
--build-arg api_collector_kafka_bootstrap=kafka:${KAFKA_PORT} \
--build-arg api_collector_mongo_url=mongo1:${MONGO_PORT} \
--build-arg spring_datasource_data_hikari_url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres \
--build-arg spring_datasource_url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres \
--build-arg spring_datasource_jdbc_url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres \
--build-arg spring_db_datasource_url=jdbc:postgresql://postgre:${POSTGRE_PORT}/postgres \
"
export BACKEND_ARG="--build-arg server_port=${BACKEND_PORT} \
${ARG_OPT} \
"
#--build-arg stream_shell_dir=${STREAM_SHELL_DIR} \
#--build-arg stream_shell_start=${STREAM_SHELL_START} \

export SCHEDULER_ARG="--build-arg server_port=${SCHEDULER_PORT} ${ARG_OPT}"

export STREAM_ARG="--build-arg server_port=${STREAM_PORT} \
--build-arg TopologyGroupName=TopologyGroup1,TopologyGroup2,TopologyGroup3 \
--build-arg limit_check_ip=${STREAM_IP} \
--build-arg limit_check_port=${STREAM_PORT} \
${ARG_OPT} \
"


