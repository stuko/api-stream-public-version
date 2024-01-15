#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
docker-compose -f ./api-elk/docker-compose.yml down
docker-compose -f ./api-elk/docker-compose.yml build
docker-compose -f ./api-elk/docker-compose.yml up -d

echo "if you seee 'stacktrace": ["java.lang.IllegalArgumentException: Limit of total fields [1000] has been exceeded' , fix index.mapping.total_fields.limit !!!! "
curl -X PUT "http://localhost:9200/_all/_settings?preserve_existing=true" -H "Content-Type: application/json" -d "{\"index.mapping.total_fields.limit\": 100000 }"
