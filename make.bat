@echo off
cls
javac src/*.java -cp build/classes -d build/classes
cd build/classes
java -Djavax.net.ssl.keyStore=../../localServerCertificate -Djavax.net.ssl.keyStorePassword=password -Djavax.net.ssl.trustStore=../../remoteServerCertificate -Djavax.net.ssl.trustStorePassword=password OTPlocalServer
cd ../..
