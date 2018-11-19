#!/bin/bash

set -exu

RP_VERSION=1.3.1
DEB=reactive-cli_$VERSION-xenial_amd64.deb

wget https://dl.bintray.com/lightbend/deb/$DEB

sudo dpkg -i $DEB
