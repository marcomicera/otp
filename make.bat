cp ../OTPclient/build/classes/UserInfos.class build/classes
@echo off
cls
javac src/*.java -cp build/classes -d build/classes
cd build/classes
java -Djavax.net.ssl.keyStore=mySrvKeystore -Djavax.net.ssl.keyStorePassword=password OTPserver
cd ../..
