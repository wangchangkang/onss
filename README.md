![微信服务商之特约商户](wechat-service-store.png)

### 准备工作
1. Nginx：80、443 onss-shop：8010 onss-store：8020 onss-manage：8030 MongoDB：27017、27018
2. JDK版本：jdk-15_linux-x64_bin.rpm
3. 图片目录 /home/picture
4. 域名证书 /home/nginx/4866716_www.1977.work.pem;/home/nginx/4866716_www.1977.work.key;
5. JAR目录 /home/onss/[服务名称+版本]
6. 日志目录 /homg/log/[服务名称]

### 配置服务

- Nginx配置文件
```shell
server {
    listen 443 ssl;
    server_name 1977.work www.1977.work; #填写绑定证书的域名
    ssl_certificate /home/nginx/4866716_www.1977.work.pem;  # 指定证书的位置，绝对路径
    ssl_certificate_key /home/nginx/4866716_www.1977.work.key;  # 绝对路径，同上
    ssl_session_timeout 5m;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2; #按照这个协议配置
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;#按照这个套件配置
    ssl_prefer_server_ciphers on;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }
    location /picture {
        alias /home/picture;
        autoindex on;
    }
    location /manage {
        proxy_pass http://127.0.0.1:8030/manage; #docker容器中的服务
        proxy_set_header Host $host:$server_port;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    location /store {
        proxy_pass http://127.0.0.1:8020/store; #docker容器中的服务
        proxy_set_header Host $host:$server_port;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    location /shop {
        proxy_pass http://127.0.0.1:8010/shop; #docker容器中的服务
        proxy_set_header Host $host:$server_port;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

```shell
server {
    listen 80;
    server_name 1977.work www.1977.work; #填写绑定证书的域名
    rewrite ^(.*)$ https://${server_name}$1 permanent;
}
```

- onss-shop配置文件

```shell script
[Unit]
Description=service-shop
After=syslog.target network.target

[Service]
ExecStart=/usr/bin/java -jar /home/onss/service-shop-0.0.1-SNAPSHOT.jar --server.port=8010 --spring.profiles.active=master

[Install]
WantedBy=multi-user.target
```

### 启动服务

```shell script
#安装端口检查工具
yum -y install net-tools
#检查JDK版本
java -version
#安装JDK
rpm -ivh jdk-8u241-linux-x64.rpm
#重新加载所有service服务
systemctl daemon-reload
#开机启动
systemctl enable onss-shop.service
#查看该service是否开机启用
systemctl is-enabled onss-shop.service
#启动服务
systemctl start onss-shop.service
#查看服务状态
systemctl status onss-shop.service
#停止服务
systemctl stop onss-shop.service
#停用服务
systemctl disable onss-shop.service
#查看异常
journalctl -u 
systemctl -u onss-shop.service -f
#查看端口
netstat -tnlp | grep 80
```

删除收到货地址
取消订单
删除收藏