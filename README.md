### docker-compose服务编排
```yaml
version: "3"
services:
  nginx:
    container_name: nginx
    image: nginx:latest
    restart: always
    ports:
      - 80:80
      - 443:443
    volumes:
      - ./nginx/conf.d:/etc/nginx/conf.d  # 配置文件
      - ./picture:/picture # 图片路径
  mongo-27017:
    image: mongo:latest
    container_name: mongo-27017
    restart: always
    ports:
      - 27017:27017
    command: mongod --replSet onss --oplogSize 128 --port 27017 # replSet 使用副本集功能
    environment:
      MONGODB_APPLICATION_DATABASE: test # 初始化DATABASE
  mongo-27018:
    image: mongo:latest
    container_name: mongo-27018
    restart: always
    ports:
      - 27018:27018
    command: mongod --replSet onss --oplogSize 128 --port 27018
    environment:
      MONGODB_APPLICATION_DATABASE: test
  mongo-init:
    image: mongo:latest
    depends_on:
      - mongo-27017
      - mongo-27018
    restart: on-failure:5
    command:
      - mongo
      - mongodb://mongo-27017:27017/admin
      - --eval
      - 'rs.initiate({ _id: "onss", members: [{_id:0,host:"mongo-27017:27017"},{_id:2,host:"mongo-27018:27018"}]})' # 初始化副本集群
  store:
    image: "java:8"
    container_name: store
    restart: always
    depends_on:
      - mongo-27017
      - mongo-27018
      - nginx
    ports:
      - 8001:8001
    volumes:
      - /Users/wangchanghao/Desktop/work/onss/picture:/picture
      - /Users/wangchanghao/Desktop/work/onss/web/store/target/store-service-0.0.1-SNAPSHOT.jar:/onss/store-service-0.0.1-SNAPSHOT.jar
    entrypoint: ["java","-jar","/onss/store-service-0.0.1-SNAPSHOT.jar"]

```

### Nginx配置文件

```shell script

server {
    listen       80;
    listen 443 ssl; 
    server_name www.1977.work localhost; #填写绑定证书的域名
    ssl_certificate /etc/nginx/conf.d/4274118_www.1977.work.pem;  # 指定证书的位置，绝对路径
    ssl_certificate_key /etc/nginx/conf.d/4274118_www.1977.work.key;  # 绝对路径，同上
    ssl_session_timeout 5m;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2; #按照这个协议配置
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;#按照这个套件配置
    ssl_prefer_server_ciphers on;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }

    location /picture {
        alias /picture; #图片路径
        autoindex on;
    }
    location /manage { 
        proxy_pass http://store:8001/manage; #docker容器中的服务
        proxy_set_header Host $host:$server_port;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

```

### application-docker.yml配置文件
```yaml

server:
  port: 8001
  servlet:
    context-path: /manage
    encoding:
      force-response: true
spring:
  data:
    mongodb:
      uri: mongodb://mongo-27017:27017,mongo-27018:27018/test?replicaSet=onss # 使用MongoDB副本集群
  main:
    lazy-initialization: true
logging:
  level:
    root: info

```

![微信服务商之特约商户](微信服务商之特约商户.png)