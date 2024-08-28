#!/bin/bash
docker image inspect coding-challenge-fullstack-api-gateway:latest >/dev/null 2>&1 && echo API Gateway image already created || docker build -t coding-challenge-fullstack-api-gateway:latest ./api-gateway/
docker image inspect coding-challenge-fullstack-user-service:latest >/dev/null 2>&1 && echo User Service image already created || docker build -t coding-challenge-fullstack-user-service:latest ./user-service/
docker image inspect coding-challenge-fullstack-operation-service:latest >/dev/null 2>&1 && echo Operation Service image already created || docker build -t coding-challenge-fullstack-operation-service:latest ./operation-service/
docker-compose up -d