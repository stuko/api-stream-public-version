#!/bin/bash
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
pid_api=`ps -ef | grep "check_api_stream" | grep -v 'grep' | awk '{print $2}'`
echo $pid_api
if [[ -z $pid_api ]]; then
   echo $(date)
   cd /home/kronos/workspace/wayd-api-collector && ./restart_mon.sh
   echo "restart mon...."
fi
