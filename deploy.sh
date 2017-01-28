#!/bin/bash

INSTANCE="$1"

if [ -z "$INSTANCE" ]; then
  echo "usage:  ./deploy.sh <instance_name>"
  exit 1;
fi

storm jar kafkatohive-0.0.1-SNAPSHOT.jar hiro.storm.demo.KafkaToHiveTopology "$INSTANCE" ./topology.properties
