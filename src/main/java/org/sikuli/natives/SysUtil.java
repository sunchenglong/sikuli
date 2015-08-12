/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sikuli.natives;

import java.lang.reflect.Constructor;

/**
 *
 * @author rhocke
 */
public class SysUtil {

  static OSUtil osUtil = null;

  static String getOSUtilClass() {
    String pkg = "org.sikuli.natives.";
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("mac")) {
      return pkg + "MacUtil";
    } else if (os.startsWith("windows")) {
      return pkg + "WinUtil";
    } else if (os.startsWith("linux")) {
      return pkg + "LinuxUtil";
    } else {
      System.out.println("[error] fatal: getOSUtilClass: your OS is not supported");
      System.exit(1);
    }
    return null;
  }

  public static OSUtil getOSUtil() {
    if (osUtil == null) {
      try {
        Class c = Class.forName(SysUtil.getOSUtilClass());
        Constructor constr = c.getConstructor();
        osUtil = (OSUtil) constr.newInstance();
      } catch (Exception e) {
        System.out.println("[error] fatal: getOSUtil\n" + e.getMessage());
        System.exit(1);
      }
    }
    return osUtil;
  }
}
