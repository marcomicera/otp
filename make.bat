@echo off
cls
javac src/*.java -cp build/classes;lib -d build/classes
cd build/classes
cp counterResponse.class ../../../OTPlocalServer/build/classes
java  -Djavax.net.ssl.keyStore=../../remoteServerCertificate -Djavax.net.ssl.keyStorePassword=password -Djavax.net.ssl.trustStore=../../localServerCertificate -Djavax.net.ssl.trustStorePassword=password -cp .;../../lib/mysql-connector-java-5.1.42-bin.jar OTPremoteServer
cd ../..
