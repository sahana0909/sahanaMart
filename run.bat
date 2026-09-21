@echo off
echo ==================================================================
echo   Starting SahanaMart E-Commerce Platform (Apache Tomcat 9)
echo ==================================================================
echo.
echo Compiling and starting embedded server on http://localhost:8080/sahanamart/ ...
echo Press Ctrl+C anytime to stop the server.
echo.

call mvn test-compile exec:java
