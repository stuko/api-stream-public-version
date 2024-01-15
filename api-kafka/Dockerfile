FROM ubuntu:16.04
MAINTAINER ParkEunyoung <eunyoung.park@koreacb.com>

RUN apt-get -y update && apt-get install -y \
    openjdk-8-jdk

ENV LC_ALL=C.UTF-8
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME/bin

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG SAFE_HOME=/app/safe

ARG PROJECT_NAME
ARG PROJECT_PATH
ARG PROJECT_HOME=$SAFE_HOME$PROJECT_PATH

ARG DIST_DIR
ARG DIST_PATH=$DIST_DIR$PROJECT_PATH

ARG ROOT_SHELL

COPY ${ROOT_SHELL}/safe-env.sh ${SAFE_HOME}/bin/

COPY ${ROOT_SHELL} ${SAFE_HOME}
WORKDIR ${SAFE_HOME}
RUN sed -i -e 's/\r$//' *.sh

COPY ${DIST_PATH} ${PROJECT_HOME}/../
WORKDIR ${PROJECT_HOME}/../
RUN tar -xvf ${PROJECT_NAME}.tar

WORKDIR ${PROJECT_HOME}
RUN sed -i -e 's/\r$//' startup.sh shutdown.sh
RUN chmod +x startup.sh shutdown.sh

ENTRYPOINT ["bash", "startup.sh", "-profile", "docker"]
