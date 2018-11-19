#!/bin/bash

set -exu

sbt 'deploy minikube'

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

REPLY=`curl --header 'Host: superservice.com' $(minikube ip)/hello/donkey`
if [ "$REPLY" = 'Hello, donkey' ]
then
  echo "Success!"
else
  echo "Got reply '$REPLY' instead of 'Hello, donkey'"
  exit -1
fi

