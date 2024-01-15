docker logs -f wayd-mariadb | while read line; do

  if [[ $line =~ 'Got an error reading communication packets' ]]; then
    echo "Got an error reading communication packets ... restart wayd-mariadb"
    cd /home/kronos/workspace/wayd-api-collector && ./clean_docker.sh
    docker restart wayd-mariadb
    sleep 1m
  fi

done;
