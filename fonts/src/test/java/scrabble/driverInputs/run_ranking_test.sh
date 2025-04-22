#!/bin/bash

# Copiar archivos .dat predefinidos a la raíz del proyecto
cp -f ./jugadores_ranking_test.dat ../../../../../jugadores.dat
cp -f ./puntuaciones_ranking_test.dat ../../../../../ranking.dat

# Subir hasta la carpeta donde está gradlew
cd ../../../../../ || exit 1

# Ejecutar el test redirigiendo la entrada y guardando el output en driverOutputs
./gradlew runTemp -PmainClass=scrabble.domain.DomainDriver < ./src/test/java/scrabble/driverInputs/RankingTest.txt > ./src/test/java/scrabble/driverOutputs/RankingOutput.txt
