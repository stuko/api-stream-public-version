#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
echo "BASEDIR is ${BASEDIR}"
docker pull postgres
docker stop postgre-api
docker rm postgre-api
docker volume create pgdata
# docker run --name postgre-api -e POSTGRES_PASSWORD=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v /sw/dsapp/docker/shell:/shell -v /sw/dsapp/docker/volume/postgres/data:/var/lib/postgresql/data/pgdata -p 5432:5432 -d postgres
# -e PGDATA=/var/lib/postgresql/data/pgdata \
docker run --name postgre-api -e "TZ=Asia/Seoul" \
-e POSTGRES_PASSWORD=postgres  ${DOCKER_OPTIONS} \
-e PGOPTIONS="-c max_connections=500" \
--mount type=bind,source=${BASEDIR}/volume/postgres/data/pgdata,target=/var/lib/postgresql/data \
--mount type=bind,source=${BASEDIR}/volume/postgres/init,target=/docker-entrypoint-initdb.d \
-p ${POSTGRE_PORT}:${POSTGRE_PORT} -d  --restart always postgres:13

# -v "${BASEDIR}"/volume/postgres/init/:/docker-entrypoint-initdb.d/ \
# -v ${BASEDIR}/volume/postgres/data/pgdata:/var/lib/postgresql/data \
# -e PGDATA=${BASEDIR}/volume/postgres/data/pgdata \
# -v ${BASEDIR}/volume/postgres/data/pgdata:/var/lib/postgresql/data/pgdata \
#docker run --name postgre-api -e POSTGRES_PASSWORD=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v postgres_data:/var/lib/postgresql/data -p 5432:5432 -d postgres

