systemctl stop firewalld
iptables -t filter -F  
iptables -t filter -X 
systemctl restart docker.service
./install_postgres.sh
./install_api_stream.sh
systemctl start firewalld
