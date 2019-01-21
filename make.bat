@echo off
cls
javac src/*.java -cp build/classes -d build/classes
cd build/classes
copy UserInfos.class ..\..\..\OTPlocalServer\build\classes /Y > NUL
copy UserInfos.class ..\..\..\OTPremoteServer\build\classes /Y > NUL
java -Djavax.net.ssl.trustStore=../../localServerCertificate -Djavax.net.ssl.trustStorePassword=password OTPclient
cd ../..
