#!/bin/bash

#sudo docker pull registry:latest
sudo docker run --name local-registry -d -p 5000:5000 registry
