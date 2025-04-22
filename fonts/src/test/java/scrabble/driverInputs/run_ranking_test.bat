@echo off
REM Copiar archivos .dat predefinidos a la raíz del proyecto
copy /Y .\jugadores_ranking_test.dat ..\..\..\..\..\jugadores.dat
copy /Y .\ranking_puntuaciones_ranking_test.dat ..\..\..\..\..\ranking.dat

REM Subir hasta la carpeta donde está gradlew.bat
cd ..\..\..\..\..

REM Ejecutar el test redirigiendo la entrada y guardando el output en driverOutputs
gradlew.bat runTemp "-PmainClass=scrabble.domain.DomainDriver" --stacktrace < .\src\test\java\scrabble\driverInputs\RankingTest.txt > .\src\test\java\scrabble\driverOutputs\RankingOutput.txt
