cd wayd-new-container-api-stream-restart
if [ ! -d './wayd-new-container-api-stream-restart/build/libs' ]; then
  ./gradlew build
fi;

cd build/libs
# nohup java -Dname=STREAM-RESTART -jar wayd-new-container-api-stream-restart-0.0.1-SNAPSHOT-jar-with-dependencies.jar &
nohup java -Dname=STREAM-RESTART -jar wayd-new-container-api-stream-restart-0.0.1-SNAPSHOT.jar &
cd ../../..
