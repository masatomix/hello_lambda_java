#!/bin/sh


#cd ../authlete-java-common && mvn clean install
#if [ $? -gt 0 ]; then
#  echo "ERROR: authlete-java-common";
#  exit 1;
#fi

cd ../authlete-java-jaxrs && mvn clean install

if [ $? -gt 0 ]; then
  echo "ERROR: authlete-java-jaxrs";
  exit 1;
fi

cd ../java-oauth-server && mvn clean install
if [ $? -gt 0 ]; then
  echo "ERROR: java-oauth-server";
  exit 1;
fi


cd ../hello_lambda_java && mvn clean package
if [ $? -gt 0 ]; then
  echo "ERROR: hello_lambda_java";
  exit 1;
fi
