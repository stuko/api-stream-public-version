docker container stop mon-grafana
docker container rm mon-grafana
docker run -d -p 3000:3000 --name mon-grafana grafana/grafana
