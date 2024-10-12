# java-ip-server

## 简介
高性能、轻量级的 IP 查询解决方案.
[gitee](https://gitee.com/ppnt/java-ip-server)|[github](https://github.com/litongjava/)

[Java-IP-Server](https://tio-boot.litongjava.com/zh/16_aio/03.html) 是基于自研 AIO（异步 IO）HTTP 服务和 ip2region 封装的 IP 查询服务器。打包后总大小为 **4.30MB**（包含数据库），无需依赖第三方 HTTP 服务（如 Servlet、Tio-Boot）。它提供高效、轻量级的 IP 查询解决方案，适合快速集成 IP 归属地查询功能的应用场景，确保系统的稳定性和性能。

## 特性

- **轻量级**：打包后仅需 4.30MB，包含所有必要的数据库文件。
- **高性能**：基于自研 AIO HTTP 服务，支持高并发请求处理。
- **独立运行**：无需依赖任何第三方 HTTP 服务，简化部署和维护。
- **易于部署**：通过 Docker 容器化部署，快速启动和扩展。

## 启动

按照以下步骤启动 `java-ip-server`：

1. **创建应用目录并进入**

    ```shell
    mkdir /data/apps/java-ip-server && cd /data/apps/java-ip-server
    ```

2. **上传 `java-ip-server-1.0.0.jar`**

    将 `java-ip-server-1.0.0.jar` 文件上传到上述创建的目录中。

3. **使用 Docker 启动服务器**

    ```shell
    docker run -dit --name ip-server --restart=always -p 10005:8080 \
    -v $(pwd):/app -w /app \
    -e TZ=Asia/Shanghai \
    -e LANG=C.UTF-8 \
    litongjava/jdk:8u391-stable-slim \
    java -jar java-ip-server-1.0.0.jar
    ```

    - **参数说明**：
        - `--name ip-server`：容器名称设为 `ip-server`。
        - `--restart=always`：容器总是重启，除非被手动停止。
        - `-p 10005:8080`：将宿主机的 `10005` 端口映射到容器的 `8080` 端口。
        - `-v $(pwd):/app`：将当前目录挂载到容器的 `/app` 目录。
        - `-w /app`：设置容器内的工作目录为 `/app`。
        - `-e TZ=Asia/Shanghai`：设置时区为上海时间。
        - `-e LANG=C.UTF-8`：设置语言环境为 UTF-8。
        - `litongjava/jdk:8u391-stable-slim`：使用指定的 JDK 8 Docker 镜像。
        - `java -jar java-ip-server-1.0.0.jar`：运行 JAR 包启动服务器。

## 运行示例

启动服务器后，可以通过以下请求查询 IP 的归属地信息：

### 查询 IP 属地信息

**请求命令**：

```shell
curl -v http://localhost:10005/ip?ip=66.75.89.81
```

**响应结果**：

```shell
美国|0|夏威夷|檀香山|0
```

**结果解析**：

- **美国**：国家
- **0**：省份（未细分）
- **夏威夷**：州/地区
- **檀香山**：城市
- **0**：ISP（互联网服务提供商，未细分）

以上结果表明，查询的 IP 地址 `66.75.89.81` 来自美国夏威夷州檀香山市。

