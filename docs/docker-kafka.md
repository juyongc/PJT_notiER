## Kafka docker-compose 분석
```yaml
services:
##Kafka 00    
Kafka00Service:
image: bitnami/kafka:3.5.1-debian-11-r44
restart: unless-stopped
container_name: Kafka00Container
ports:
- '10000:9094'
environment:
- KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true
## KRaft settings
# 고유 식별자 설정
- KAFKA_CFG_BROKER_ID=0
- KAFKA_CFG_NODE_ID=0
# 클러스터 설정
- KAFKA_KRAFT_CLUSTER_ID=HsDBs9l6UUmQq7Y5E6bNlw
# 클러스터 퀴럼 설정 - Raft 퀴럼 구성하는 노드 목록 정의
- KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@Kafka00Service:9093,1@Kafka01Service:9093,2@Kafka02Service:9093
# 브로커 수행 역할 정의
- KAFKA_CFG_PROCESS_ROLES=controller,broker

## Listeners
- ALLOW_PLAINTEXT_LISTENER=yes
- KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
- KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://Kafka00Service:9092,EXTERNAL://127.0.0.1:10000
- KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT
- KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
# - KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT 용도?
## Clustering
- KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR=3
- KAFKA_CFG_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=3
- KAFKA_CFG_TRANSACTION_STATE_LOG_MIN_ISR=2

networks:
- kafka_network
volumes:
- "Kafka00:/bitnami/kafka"

KafkaWebUiService:
image: provectuslabs/kafka-ui:latest
restart: always
container_name: KafkaWebUiContainer
ports:
- '8080:8080'
environment:
- KAFKA_CLUSTERS_0_NAME=Local-Kraft-Cluster
- KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=Kafka00Service:9092,Kafka01Service:9092,Kafka02Service:9092
- DYNAMIC_CONFIG_ENABLED=true
- KAFKA_CLUSTERS_0_AUDIT_TOPICAUDITENABLED=true
- KAFKA_CLUSTERS_0_AUDIT_CONSOLEAUDITENABLED=true
#- KAFKA_CLUSTERS_0_METRICS_PORT=9999
depends_on:
- Kafka00Service
- Kafka01Service
- Kafka02Service
networks:
- kafka_network


```

## Kafka 실행 테스트

```shell
docker-compose exec -it Kafka01Service /bin/bash

// 토픽 생성 (name = error-messages)
kafka-topics.sh --create --topic error-messages --bootstrap-server Kafka00Service:9092,Kafka01Service:9092,Kafka02Service:9092 --partitions 3 --replication-factor 2

kafka-console-producer.sh --topic error-messages --bootstrap-server Kafka00Service:9092,Kafka01Service:9092,Kafka02Service:9092

kafka-console-consumer.sh --topic error-messages --from-beginning --bootstrap-server Kafka00Service:9092,Kafka01Service:9092,Kafka02Service:9092

```