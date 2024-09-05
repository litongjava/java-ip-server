package com.litongjava.ip.utils;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lionsoul.ip2region.xdb.Searcher;

public enum Ip2RegionUtils {
  INSTANCE;

  private static Searcher searcher;
  static {
    URL resource = Ip2RegionUtils.class.getClassLoader().getResource("ipdb/ip2region.xdb");
    if (resource != null) {
      try {
        byte[] bytes = FileUtil.readUrlAsBytes(resource);
        searcher = Searcher.newWithBuffer(bytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public static boolean checkIp(String ipAddress) {
    String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    Pattern pattern = Pattern.compile(ip);
    Matcher matcher = pattern.matcher(ipAddress);
    return matcher.matches();
  }

  public static String searchIp(long ip) {
    if (searcher != null) {
      try {
        return searcher.search(ip);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  public static String searchIp(String ip) {
    if ("0:0:0:0:0:0:0:1".equals(ip)) {
      return "0|0|0|内网IP|内网IP";
    }
    if (searcher != null) {
      try {
        return searcher.search(ip);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
