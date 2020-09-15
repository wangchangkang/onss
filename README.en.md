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