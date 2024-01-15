sudo docker system prune --all --force
sudo sh -c 'truncate -s 0 /var/lib/docker/containers/*/*-json.log'
