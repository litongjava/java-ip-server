package com.litongjava.ip.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtil {

  /**
   * 从URL读取数据并返回字节数组
   * 
   * @param resource URL资源
   * @return 字节数组
   * @throws IOException 读取过程中可能抛出的异常
   */
  public static byte[] readUrlAsBytes(URL resource) throws IOException {
    if (resource == null) {
      throw new IllegalArgumentException("资源URL不能为空");
    }

    try (InputStream inputStream = resource.openStream(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int bytesRead;

      // 读取URL内容到字节数组
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }

      return byteArrayOutputStream.toByteArray();
    }
  }
}
