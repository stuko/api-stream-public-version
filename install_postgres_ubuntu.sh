#!/bin/bash

BASEDIR=$(dirname $0)
source ${BASEDIR}/set-env-ubuntu.sh
echo "API HOME is ${API_HOME}"
echo "PORT is ${POSTGRE_PORT}"
docker pull postgres
docker stop postgre-api
docker rm postgre-api
docker volume create pgdata
# docker run --name postgre-api -e POSTGRES_PASSWORD=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v /sw/dsapp/docker/shell:/shell -v /sw/dsapp/docker/volume/postgres/data:/var/lib/postgresql/data/pgdata -p 5432:5432 -d postgres
# -e PGDATA=/var/lib/postgresql/data/pgdata \
docker run --name postgre-api -e "TZ=Asia/Seoul" \
-e POSTGRES_PASSWORD=postgres  ${DOCKER_OPTIONS} \
-v ${BASEDIR}/volume/postgres/data/pgdata:/var/lib/postgresql/data \
-v ${BASEDIR}/volume/postgres/init/:/docker-entrypoint-initdb.d/ \
-p ${POSTGRE_PORT}:${POSTGRE_PORT} -d postgres

# -e PGDATA=${BASEDIR}/volume/postgres/data/pgdata \
# -v ${BASEDIR}/volume/postgres/data/pgdata:/var/lib/postgresql/data/pgdata \
#docker run --name postgre-api -e POSTGRES_PASSWORD=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v postgres_data:/var/lib/postgresql/data -p 5432:5432 -d postgres

