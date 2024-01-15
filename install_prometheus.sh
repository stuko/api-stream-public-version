docker container stop mon-prom
docker container rm mon-prom
docker run -d -p 9090:9090 -v /sw/dsapp/docker/volume/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml --name mon-prom prom/prometheus

