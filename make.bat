@echo off
cls
javac src/*.java -cp build/classes -d build/classes
cd build/classes
xcopy UserInfos.class ../../../OTPlocalServer/build/classes
xcopy UserInfos.class ../../../OTPremoteServer/build/classes
java -Djavax.net.ssl.trustStore=../../localServerCertificate -Djavax.net.ssl.trustStorePassword=password OTPclient
cd ../..
