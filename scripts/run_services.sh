#!/bin/bash
echo "demarrage des conteneur Docker"
cd "$(dirname "$0")"

sudo docker-compose up -d
sleep 5

echo "lancement du service PostgreSQL"
cd ..
cd postgresql
sudo ./run_postgresql.sh
cd ..

echo "lancement du srv-translate..."
cd srv-translate
sudo ./mvnw clean package -DskipTests
cd target
sudo nohup java -jar srvtranslate-0.0.1-SNAPSHOT.jar > srv-translate.log 2>&1 &
cd ../..

echo "Compilation et lancement de client-cons-db..."
cd cons-db-srv
sudo ./mvnw clean package -DskipTests
cd target
sudo nohup java -jar clientConsDb-0.0.1-SNAPSHOT.jar > client-cons-db.log 2>&1 &
cd ../..

echo "les services sont lancés !lancer les clients CLI"
echo "Pour ClientA : ./5a_run_cliA.sh ou B, C,D"
