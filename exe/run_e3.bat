@echo off
set FXPATH=javafx-sdk-24.0.1\lib
java --module-path "%FXPATH%" ^
 --add-modules javafx.controls,javafx.fxml,javafx.media ^
 -Dprism.order=sw ^
 -Dprism.verbose=true ^
 -Dprism.forceGPU=false ^
 -jar scrabble_e3.jar
pause
