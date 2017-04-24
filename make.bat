@echo off
cls
javac src/*.java -cp build/classes -d build/classes
cd build/classes
java -Djavax.net.ssl.trustStore=mySrvKeystore -Djavax.net.ssl.trustStorePassword=password OTPclient
cd ../..
