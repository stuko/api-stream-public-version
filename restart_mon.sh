./stop_mon.sh
sleep 5
nohup ./check_api_stream.sh  > /dev/null 2>&1 &

