package com.litongjava.ip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.litongjava.ip.utils.Ip2RegionUtils;

public class AioHttpServerJava8 {

  public static void main(String[] args) {
    try {
      // 创建异步服务器通道并绑定到端口8080
      AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
      serverChannel.bind(new InetSocketAddress(8080));

      System.out.println("AIO HTTP Server started on port 8080...");

      // 使用非阻塞模式接受连接
      serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
          if (clientChannel != null && clientChannel.isOpen()) {
            handleRequest(clientChannel);
          }
          // 再次调用 accept()，以继续接受新连接
          serverChannel.accept(null, this);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
          exc.printStackTrace();
        }
      });

      // 防止主线程退出
      Thread.currentThread().join();

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  // 处理客户端连接
  private static void handleRequest(AsynchronousSocketChannel clientChannel) {
    ByteBuffer buffer = ByteBuffer.allocate(4096); // 增加 buffer 大小以处理较大请求

    clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
      @Override
      public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        String request = StandardCharsets.UTF_8.decode(attachment).toString();

        // 解析请求路径
        String requestPath = getRequestPath(request);
        if ("/ip".equals(requestPath)) {
          ipHandler(clientChannel, request);
          // 发送响应
          return;
        }

        // 其他路径,返回404
        writeHttpResponse(clientChannel, 404, "text/plain", "404 Not Found");

      }

      @Override
      public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
      }
    });
  }

  private static void ipHandler(AsynchronousSocketChannel clientChannel, String request) {
    // 解析请求，获取IP参数
    Map<String, String> requestMap = getRequestMap(request);
    String ip = requestMap.get("ip");

    String body = null;
    if (ip != null && !ip.isEmpty()) {
      body = Ip2RegionUtils.searchIp(ip);
    }
    // 构建HTTP响应
    writeHttpResponse(clientChannel, 200, "text/plain;charset=utf-8", body);
  }

  // 从请求中提取请求路径
  private static String getRequestPath(String request) {
    String[] lines = request.split("\r\n");
    for (String line : lines) {
      if (line.startsWith("GET") || line.startsWith("POST")) {
        String[] parts = line.split(" ");
        if (parts.length > 1) {
          String query = parts[1];
          return query.split("\\?")[0]; // 提取请求路径
        }
      }
    }
    return "/";
  }

  // 从请求中提取参数并封装为Map的方法
  private static Map<String, String> getRequestMap(String request) {
    Map<String, String> paramMap = new HashMap<>();
    String[] lines = request.split("\r\n");
    for (String line : lines) {
      if (line.startsWith("GET") || line.startsWith("POST")) {
        String[] parts = line.split(" ");
        if (parts.length > 1) {
          String query = parts[1];
          if (query.contains("?")) {
            String[] queryParams = query.substring(query.indexOf("?") + 1).split("&");
            for (String param : queryParams) {
              String[] keyValue = param.split("=");
              if (keyValue.length > 1) {
                paramMap.put(keyValue[0], keyValue[1]);
              } else {
                paramMap.put(keyValue[0], ""); // 如果没有值，则设为空字符串
              }
            }
          }
        }
      }
    }
    return paramMap;
  }

  private static void writeHttpResponse(AsynchronousSocketChannel clientChannel, int statusCode, String contentType, String body) {

    String statusMessage;
    switch (statusCode) {
    case 200:
      statusMessage = "OK";
      break;
    case 404:
      statusMessage = "Not Found";
      break;
    case 500:
      statusMessage = "Internal Server Error";
      break;
    default:
      statusMessage = "Unknown";
    }

    String response;
    String string = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" + "Content-Type: " + contentType + "\r\n";

    if (body != null) {
      byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
      response = string + "Content-Length: " + bytes.length + "\r\n" + "\r\n" + body;
    } else {
      response = string + "Content-Length: 0" + "\r\n" + "\r\n";
    }

    ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
    clientChannel.write(responseBuffer, responseBuffer, new CompletionHandler<Integer, ByteBuffer>() {
      @Override
      public void completed(Integer result, ByteBuffer buffer) {
        try {
          clientChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void failed(Throwable exc, ByteBuffer buffer) {
        exc.printStackTrace();
      }
    });
  }
}
