docker service rm api-stream
docker service create --name api-stream \
--replicas 3 \
--host=kafka1:192.168.57.243 \
--host=kafka2:192.168.57.243 \
--host=kafka3:192.168.57.243 \
--host=mongo1:192.168.57.243 \
--host=mongo2:192.168.57.243 \
--host=mongo3:192.168.57.243 \
--hostname=localhost  \
#--host=ktagap3x:192.168.120.211 \
#--host=ktagap4x:192.168.120.212 \
#--host=ktagap5x:192.168.120.213 \
--host=kafka:192.168.57.243 \
--host=zookeeper:192.168.57.243 \
--host=postgre:192.168.57.243 \
--host=backend:192.168.57.243 \
--host=scheduler:192.168.57.243 \
#--host=opendart.fss.or.kr:61.73.60.206 \
--mount type=bind,source=/etc/localtime,target=/etc/localtime,readonly \
--publish target=8120,published=8120 \
-e PROFILES=dev \
-e MIN=512m \
-e MAX=1024m \
-e TopologyGroupName=TopologyGroup1,TopologyGroup2,TopologyGroup3 \
api/api-collector-stream-docker
##docker service logs -f --tail 10 api-stream >> stream_log_20201026.log
