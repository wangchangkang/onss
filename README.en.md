更新yum
sudo yum update
执行docker安装脚本
curl -sSL https://get.docker.com/ | sh
启动docker服务d
sudo service docker start 

没有python-pip包就执行如下命令安装epel-release依赖
yum -y install epel-release
安装python-pip
yum -y install python-pip
安装好的pip组件进行升级
sudo pip install --upgrade pip
安装openssl-devel
sudo yum install gcc libffi-devel python-devel openssl-devel -y
安装docker-compose
sudo pip install docker-compose

开启远程连接
/lib/systemd/system/docker.service 
-H fd:// 改为 -H tcp://0.0.0.0:2375
重新加载配置文件
systemctl daemon-reload
重启docker
service docker restart
外网访问docker信息
http://49.232.165.242:2375/version

IDEA连接docker
http://49.232.165.242:2375

vim /etc/docker/daemon.json

```json
   {
     "debug": true,
     "experimental": false,
     "registry-mirrors": [
       "https://registry.docker-cn.com",
       "https://eqcxmbvw.mirror.aliyuncs.com"
     ]
   }
   ```

sudo systemctl daemon-reload
service docker restart

配置mongo
docker run -p 0.0.0.0:27017:27017 --name mongo mongo:latest
