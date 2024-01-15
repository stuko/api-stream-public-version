# wayd-api-collector
> api-collector는 API 기반으로 제공하는 정보들을 누구나 쉽게 수집할 수 있도록 제공해 드리는 솔루션입니다.

> 모두 도커 기반으로 실행 됩니다. (도커 설치 필수)

> 대용량 스트림 처리를 위해, Docker Swam 혹은 K8S에 운영 가능 합니다.

> 사용된 오픈소스들
> > Nginx

> > PostgreSQL

> > MongoDB

> > Kafka

> > Quartz

> > Apache Storm

> > Spring Boot

> > Frontend : React  , Backend : Java & Kotlin

> > Container : Docker (Swarm)

# installation
> just run    
> sudo ./install_all.sh


> Please check docker, react(npm, react, react-dom)


> environment : Maybe linux will be good

```
./install_all.sh
최초 설치후, http://{IP}:8090/ 으로 접속후  admin/asdfasdf11 로그인 가능합니다.
```

```
npm or nodejs 설치가 되어 있지 않은 경우
sudo yum install npm nodejs
react 설치가 되어 있지 않은 경우
sudo npm install react react-dom --save
npm run build

도커 컴포저 설치가 되어 있지 않은 경우
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

Mongo DB 에 스트림 로그를 저장 할 경우   
install_api_stream.sh 파일의 -e STREAM_LOG_DEBUG=true 로 설정 하면 됨.
```

# 스트림 기반 API 수집 플랫폼 시스템 개요 
> ![image](https://user-images.githubusercontent.com/1683771/164582329-e4680ad7-b943-450d-9be2-b585d2966a59.png)

> API 프로세스를 설계하는 화면 예시 : gojs 유료 버전을 구매후 사용해 주세요.(워터마크 제거시)
![image](https://user-images.githubusercontent.com/1683771/164581402-ee2ed399-118f-4ab3-878d-11dd9cc33986.png)

#### API 스트림 메인 화면에 참고 하실 수 있는 동영상이 연결되어 있습니다 ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/d7c85e3d-3ad6-4e1e-9f0d-e8a474ea2ea3

#### 수집할 API 정보(URL, API인증키)를 입력하는 동영상입니다. ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/3bfcf10a-47cc-4a7b-aad6-f66d52ecc87c

#### API 인증키 값을 관리하는 동영상 입니다. ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/927b2d3e-4164-45eb-b1e7-b1e9a4586883

#### API 수집을 위한 프로세스정보와 수집결과 정보를 MongoDB에서 확인하는 동영상입니다.  ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/9cc31952-1652-4c35-9289-19170cf633ef

#### API 수집 스케줄을 등록해 주는 동영상입니다. ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/cbebe558-7a4f-4ac0-981d-18ed19a2a196

#### API 수집 프로세스를 Diagram으로 그리고, 설정하는 동영상 입니다. ####
> https://github.com/stuko/api-stream-public-version/assets/1683771/18ed2fde-797f-418a-8042-59ce71e9eec8
