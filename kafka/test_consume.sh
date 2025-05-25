#!/bin/bash
docker exec -it projet-messagerie-kafka-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic topicin \
  --from-beginning
