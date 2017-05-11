cp ../OTPclient/build/classes/UserInfos.class build/classes
@echo off
cls
javac src/*.java -cp build/classes;lib -d build/classes
cd build/classes
java  -Djavax.net.ssl.keyStore=remoteServerCertificate -Djavax.net.ssl.keyStorePassword=password -cp .;../../lib/mysql-connector-java-5.1.42-bin.jar OTPremoteServer
cd ../..
