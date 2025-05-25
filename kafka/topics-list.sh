#!/bin/bash
docker exec -it projet-messagerie-kafka-1 kafka-topics \
  --bootstrap-server localhost:9092 \
  --list
