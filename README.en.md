```shell script
23#移除旧docker信息
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
#安装docker需要的依赖
sudo yum install -y yum-utils
#设置docker安装源
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
sudo yum-config-manager --enable docker-ce-nightly
#降低containerd.io版本，不然安装docker会失败
yum install -y https://mirrors.aliyun.com/docker-ce/linux/centos/7/x86_64/edge/Packages/containerd.io-1.2.13-3.1.el7.x86_64.rpm
#安装docker
sudo yum install docker-ce docker-ce-cli containerd.io
#下载并安装docker-compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.27.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 开放远程控制 /usr/lib/systemd/system/docker.service
# ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
# ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
vi /etc/docker/daemon.json
{
"registry-mirrors": ["http://hub-mirror.c.163.com"]
}

#加载配置文件
systemctl daemon-reload
#开机自启动
systemctl enable docker
#查看服务是否开机自启动
systemctl is-enabled docker
#重启
sudo systemctl restart docker
#查看服务状态
systemctl status docker
#停止服务
systemctl stop docker
#停用服务
systemctl disable docker
#查看异常
journalctl -xe
#查看端口
netstat -tnlp | grep 8092375

```
### 安装 nginx 1.8.0

```shell script
vim /etc/yum.repos.d/nginx.repo 


[nginx-stable]
name=nginx stable repo
baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
gpgcheck=1
enabled=1
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true

[nginx-mainline]
name=nginx mainline repo
baseurl=http://nginx.org/packages/mainline/centos/$releasever/$basearch/
gpgcheck=1
enabled=0
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true


yum info nginx
yum install nginx
systemctl start nginx #启动
systemctl stop nginx #停止
systemctl reload nginx #重启
systemctl status nginx #状态
systemctl enable nginx #自启

vim /etc/yum.repos.d/mongodb-org-4.4.repo

[mongodb-org-4.4]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/4.4/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.4.asc


```

### 安装 JDK
```shell script
rpm -ivh jdk-15_linux-x64_bin.rpm
```
chmod +x /home/shop-service-0.0.1-SNAPSHOT.jar 
ln -s /home/shop-service-0.0.1-SNAPSHOT.jar /etc/init.d/shop

chmod +x /home/store-service-0.0.1-SNAPSHOT.jar 
ln -s /home/store-service-0.0.1-SNAPSHOT.jar /etc/init.d/store