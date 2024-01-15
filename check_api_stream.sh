docker logs -f api-stream | while read line; do

  if [[ $line =~ 'Could not find leader nimbus from seed hosts' ]]; then
    echo "can not find leader ... restart api-stream"
    docker restart api-stream
    sleep 1m
  fi

  if [[ $line =~ 'Nimbus - not a leader, skipping cleanup' ]]; then
    echo "not a leader ... restart api-stream"
    docker restart api-stream
    sleep 1m
  fi

done;
