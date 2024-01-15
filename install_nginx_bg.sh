#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
echo "NGINX IP is ${NGINX_IP}"
docker pull nginx
docker stop nginx-api
docker rm nginx-api
#docker run -d --name nginx-api --restart always -v /sw/dsapp/docker/volume/nginx/www:/usr/share/nginx/html -v /sw/dsapp/docker/volume/nginx/log:/etc/nginx/logs -v /sw/dsapp/docker/volume/nginx/conf:/etc/nginx/conf -v /sw/dsapp/docker/volume/nginx/conf.d:/etc/nginx/conf.d -p 8090:8090 nginx
# docker run --add-host=ktagap3x:192.168.120.211 --add-host=ktagap4x:192.168.120.212 --add-host=ktagap5x:192.168.120.213 --add-host=kafka:192.168.120.211 --add-host=zookeeper:192.168.120.211 --add-host=mongo:192.168.120.211 --add-host=postgre:192.168.120.211 --add-host=backend:192.168.120.211 --add-host=scheduler:192.168.120.211 --add-host=opendart.fss.or.kr:61.73.60.206 -d -p 8090:8090 --name nginx-api -v /sw/dsapp/docker/volume/nginx/www:/usr/share/nginx/html -v /sw/dsapp/docker/volume/nginx/log:/var/log/nginx -v /sw/dsapp/docker/volume/nginx/conf:/etc/nginx/conf -v /sw/dsapp/docker/volume/nginx/conf.d:/etc/nginx/conf.d -v /sw/dsapp/docker/shell:/sw/dsapp/docker/shell firesh/nginx-lua
docker run -d --name nginx-api -e "TZ=Asia/Seoul" -p ${NGINX_PORT}:${NGINX_PORT}  \
 --restart always --add-host=nginx:${NGINX_IP}  ${DOCKER_OPTIONS} \
--mount type=bind,source=${BASEDIR}/volume/nginx/www,target=/etc/nginx/html \
--mount type=bind,source=${BASEDIR}/volume/nginx/log,target=/etc/nginx/logs \
--mount type=bind,source=${BASEDIR}/volume/nginx/conf,target=/etc/nginx/conf \
--mount type=bind,source=${BASEDIR}/volume/nginx/conf.d,target=/etc/nginx/conf.d \
nginx

#-v ${BASEDIR}/volume/nginx/www:/etc/nginx/html \
#-v ${BASEDIR}/volume/nginx/log:/etc/nginx/logs \
#-v ${BASEDIR}/volume/nginx/conf:/etc/nginx/conf \
#-v ${BASEDIR}/volume/nginx/conf.d:/etc/nginx/conf.d nginx
