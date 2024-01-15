CHECK=`sudo docker ps | grep api-stream`
if [ -z "$CHECK" ]; then
    echo "api-stream is null"
    ./docker-restart.sh
fi
