#!/bin/bash

set -exu

sbt docker:publishLocal

kubectl apply -f kubernetes/grpcservice.yml
kubectl apply -f kubernetes/httptogrpc.yml

for i in {1..10}
do
  echo "Waiting for pods to get ready..."
  kubectl get pods
  [ `kubectl get pods | grep Running | wc -l` -eq 2 ] && break
  sleep 4
done

if [ $i -eq 10 ]
then
  echo "Pods did not get ready"
  exit -1
fi

for i in {1..10}
do
  REPLY=`curl --header 'Host: superservice.com' $(sudo -E minikube ip)/hello/donkey`
  [ "$REPLY" = 'Hello, donkey' ] && break
  sleep 4
done  

if [ $i -eq 10 ]
then
  echo "Got reply '$REPLY' instead of 'Hello, donkey'"
  exit -1
fi
