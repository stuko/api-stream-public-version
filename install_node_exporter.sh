docker container stop mon-node
docker container rm mon-node
docker run -d -p 9100:9100 --name mon-node prom/node-exporter
