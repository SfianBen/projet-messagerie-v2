#!/bin/bash
export application_monnom=ClientB
#  dossier du client
cd "$(dirname "$0")/../shellClient"

#  Compilation automatique si le JAR n'existe pas
if [ ! -f target/shellClient-0.0.1.jar ]; then
    echo " Compilation du client (shellClient)..."
    ./mvnw clean package -DskipTests
fi

#  Lancement du client
cd target
java -jar shellClient-0.0.1.jar