#### Windows 10 子系统 安装 docker
```shell script
sudo apt-get update
sudo apt-get remove docker docker-engine docker.io
sudo apt-get install \
      apt-transport-https \
      ca-certificates \
      curl \
      software-properties-common
sudo apt remove gpg
sudo apt install gnupg1
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
     "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
     $(lsb_release -cs) \
     stable"
sudo apt-get update
sudo apt-get install docker-ce
```

![微信服务商之特约商户](微信服务商之特约商户.png)