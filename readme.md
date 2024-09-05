# java-ip-server
## 简介

[简介](https://tio-boot.litongjava.com/zh/16_aio/03.html)

## 启动
```shell
docker run -dit --name tio-boot-web-hello --restart=always --net=host \
-v $(pwd):/app -w /app \
-e TZ=Asia/Shanghai \
-e LANG=C.UTF-8 \
litongjava/jdk:8u391-stable-slim \
java -jar java-ip-server-1.0.0.jar
```
## 运行示例

启动服务器后，通过以下请求查询 IP 属地信息：

```shell
http://localhost:8080/ip?ip=66.75.89.81
```

响应结果示例如下：

```shell
美国|0|夏威夷|檀香山|0
```

此结果表明查询的 IP 地址来自美国夏威夷檀香山市。
