ps -ef | grep check_api_stream.sh | grep -v grep | awk '{print $2}' | xargs kill -9
