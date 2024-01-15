#!/bin/bash

BASEDIR=$(pwd)
source ${BASEDIR}/set-env.sh
echo "API HOME is ${API_HOME}"
cd api-collector-frontend

echo "Do you want install react? : [y/n]"
read -r
F_YN=$F_REPLY
if [[ "$F_YN" = "y" ]]; then
  echo "OK, install react..."
  echo "Please execut : npm install react react-dom --save"
  npm install react react-dom --save  
fi
echo "if install error then exe npm install react react-dom --save "
echo "if build error then exe npm install react-scripts --save "

echo "Do you want update npm? : [y/n]"
read -r
F_YN=$F_REPLY
if [[ "$F_YN" = "y" ]]; then
  echo "OK, update npm..."
  echo "Please execut : npm update"
  npm update
fi

echo "if install error then exe npm update "

npm run build
# xcopy .\build\*.*  %CD%\..\volume\nginx\www\ /e /h /k /y
cp -rf ./build/* ${BASEDIR}/volume/nginx/www/
cd ..
