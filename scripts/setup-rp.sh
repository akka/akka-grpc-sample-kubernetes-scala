#!/bin/bash

set -exu

# See https://developer.lightbend.com/docs/lightbend-orchestration/current/setup/cli-installation.html#install-the-cli for instructions for your platform

wget -qO - https://downloads.lightbend.com/rp/keys/bintray-debian | \
    sudo apt-key add - && \
    echo "deb https://dl.bintray.com/lightbend/deb $(lsb_release -cs) main" | \
    sudo tee /etc/apt/sources.list.d/lightbend.list && \
    sudo apt-get install apt-transport-https -y && \
    sudo apt-get update && \
    sudo apt-get install reactive-cli
