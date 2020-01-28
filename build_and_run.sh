#!/bin/bash

set -e

sudo docker stop service-dtu-pay

mvn clean test package

sudo docker build --tag service-dtu-pay .
sudo docker-compose up -d