@echo off
REM Copiar archivos .dat predefinidos a la raíz del proyecto
copy /Y .\puntuaciones_ranking_test.dat ..\..\..\..\main\resources\persistencias\ranking.dat

REM Subir hasta la carpeta donde está gradlew.bat
cd ..\..\..\..\..

REM Ejecutar el test redirigiendo la entrada y guardando el output en driverOutputs
gradlew.bat runTemp "-PmainClass=scrabble.domain.DomainDriver" --stacktrace < .\src\test\java\scrabble\driverInputs\RankingInput.txt > .\src\test\java\scrabble\driverOutputs\RankingOutput.txt
