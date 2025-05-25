#!/bin/bash
docker exec -it projet-messagerie-kafka-1 kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic topicout
