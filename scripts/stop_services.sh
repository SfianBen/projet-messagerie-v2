#!/bin/bash
echo " Arret des services Spring Boot"

sudo pkill -f srvtranslate-0.0.1-SNAPSHOT.jar
sudo pkill -f clientConsDb-0.0.1-SNAPSHOT.jar

rm -f srv-translate/target/srv-translate.log
rm -f cons-db-srv/target/client-cons-db.log

echo "Arret de Docker"
sudo docker-compose down

echo "Arret de PostgreSQL"
cd ..

cd postgresql
sudo ./stop_postgresql.sh
cd ..

echo "arreté "
