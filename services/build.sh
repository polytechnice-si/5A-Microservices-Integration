#!/bin/sh

#PUSH=true
PUSH=false

build() { # $1: directory, $2: image_name
  cd $1
  docker build -t $2 .
  if [ "$PUSH" = "true" ]; then docker push $2; fi
  cd ..
}

# Compile services code
mvn -q clean package

# Build docker images
build rpc       petitroll/tcs-rpc
build resource  petitroll/tcs-rest
