/**
 *
 * No Copyright.
 *
 * @Author : summerxiaqing
 *
 * @Description :
 *
 */

package com.buddhism.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author summerxiaqing
 */
public class ZipUtils {
  static final int BUFFER = 2048;

  public static void unZip(String fileName, String unZipPath) {
    BufferedOutputStream dest = null;
    BufferedInputStream is = null;

    try {
      // 先判断目标文件夹是否存在，如果不存在则新建，如果父目录不存在也新建
      File f = new File(unZipPath);
      if (!f.exists()) {
        f.mkdirs();
      }

      ZipEntry entry;
      ZipFile zipfile = new ZipFile(fileName);
      Enumeration<? extends ZipEntry> e = zipfile.entries();
      while (e.hasMoreElements()) {
        entry = e.nextElement();
        is = new BufferedInputStream(zipfile.getInputStream(entry));
        int count;
        byte data[] = new byte[BUFFER];
        FileOutputStream fos = new FileOutputStream(unZipPath + "/"
            + entry.getName());
        dest = new BufferedOutputStream(fos, BUFFER);
        while ((count = is.read(data, 0, BUFFER)) != -1) {
          dest.write(data, 0, count);
        }
        dest.flush();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        dest.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean makeDir(String unZipDir) {
    boolean b = false;
    try {
      File f = new File(unZipDir);
      if (!f.exists()) {
        b = f.mkdirs();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return b;
    }
    return b;
  }
}
