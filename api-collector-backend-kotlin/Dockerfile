FROM openjdk:8
ARG server_port
ARG spring_kafka_bootstrap_servers
ARG api_collector_mongo_url
ARG api_collector_kafka_broker
ARG api_collector_kafka_port
ARG api_collector_kafka_bootstrap
ARG api_collector_mongo_url
ARG spring_datasource_data_hikari_url
ARG spring_datasource_url
ARG spring_datasource_jdbc_url
ARG spring_db_datasource_url
ARG stream_shell_dir
ARG stream_shell_start
ENV PROFILES default
ENV server_port=$server_port
ENV spring_kafka_bootstrap_servers=$spring_kafka_bootstrap_servers
ENV api_collector_mongo_url=$api_collector_mongo_url
ENV api_collector_kafka_broker=$api_collector_kafka_broker
ENV api_collector_kafka_port=$api_collector_kafka_port
ENV api_collector_kafka_bootstrap=$api_collector_kafka_bootstrap
ENV api_collector_mongo_url=$api_collector_mongo_url
ENV spring_datasource_data_hikari_url=$spring_datasource_data_hikari_url
ENV spring_datasource_url=$spring_datasource_url
ENV spring_datasource_jdbc_url=$spring_datasource_jdbc_url
ENV spring_db_datasource_url=$spring_db_datasource_url
ENV stream_shell_dir=$stream_shell_dir
ENV stream_shell_start=$stream_shell_start
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java" \
,"-Dspring.profiles.active=${PROFILES}" \
,"-Dserver.port=${server_port}" \
,"-Dspring.kafka.bootstrap-servers=${spring_kafka_bootstrap_servers}" \
,"-Dapi.collector.mongo.url=${api_collector_monogo_url}}" \
,"-Dapi.collector.kafka.broker=${api_collector_kafka_broker}" \
,"-Dapi.collector.kafka.port=${api_collector_kafka_port}" \
,"-Dapi.collector.kafka.bootstrap=${api_collector_kafka_bootstrap}" \
,"-Dapi.collector.mongo.url=${api_collector_mongo_url}" \
,"-Dspring.datasource.data.hikari.url=${spring_datasource_data_hikari_url}" \
,"-Dspring.datasource.url=${spring_datasource_url}" \
,"-Dspring.datasource.jdbc-url=${spring_datasource_jdbc_url}" \
,"-Dspring.db.datasource.url=${spring_db_datasource_url}" \
,"-Dcmd.dir=${stream_shell_dir}" \
,"-Dcmd.stream.start=${stream_shell_start}" \
,"-jar","/app.jar"]
