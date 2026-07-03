@ECHO OFF
CD /D %~dp0
CALL gradlew.bat assembleDebug
IF ERRORLEVEL 1 EXIT /B 1
ECHO APK created at app\build\outputs\apk\debug\app-debug.apk
