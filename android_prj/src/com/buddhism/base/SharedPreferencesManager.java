package com.buddhism.base;

import android.content.Context;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * No Copyright
 * @Author : junfengli
 * @Description : SharedPreferencesManager is used to manager write and read
 *              SharedPreferences value,
 */

public class SharedPreferencesManager {
  private static Context sContext = null;
  private static SharedPreferencesManager sInstance = null;

  public static void createInstance(Context context) {
    if (sInstance == null) {
      sInstance = new SharedPreferencesManager(context);
    }
  }

  private SharedPreferencesManager(Context context) {
    sContext = context;
  }

  /**
   * 获取序列化保存的配置类
   * @param key
   * @param 默认值
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T getSerializable(String key, T defValue) {

    String valueString = SharedPreferencesManager.getString(key, key, defValue.toString());
    if (valueString == null || valueString.length() == 0) {
      saveSerializable(key, defValue);
      return defValue;
    }
    try {
      Object resultObject = fromString(valueString);
      return (T) resultObject;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return defValue;
  }

  /**
   * 序列化保存配置类
   * @param key
   * @param 当前值
   * @return
   */
  public static <T extends Serializable> boolean saveSerializable(String key, T value) {
    if (value == null) {
      return false;
    }
    try {
      String valueString = toString(value);
      SharedPreferencesManager.putString(key, key, valueString);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean getBoolean(String name, String key, boolean defValue) {
    if (sInstance != null && sContext != null) {
      return sContext.getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }
    return defValue;
  }

  public static String getString(String name, String key, String defValue) {
    if (sInstance != null && sContext != null) {
      return sContext.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, defValue);
    }
    return defValue;
  }

  public static int getInt(String name, String key, int defValue) {
    if (sInstance != null && sContext != null) {
      return sContext.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, defValue);
    }
    return defValue;
  }

  public static boolean putInt(String name, String key, int value) {
    if (sInstance != null && sContext != null) {
      return sContext
          .getSharedPreferences(name, Context.MODE_PRIVATE)
          .edit()
          .putInt(key, value)
          .commit();
    }
    return false;
  }

  public static boolean putBoolean(String name, String key, boolean value) {
    if (sInstance != null && sContext != null) {
      return sContext
          .getSharedPreferences(name, Context.MODE_PRIVATE)
          .edit()
          .putBoolean(key, value)
          .commit();
    }
    return false;
  }

  public static boolean putString(String name, String key, String value) {
    if (sInstance != null && sContext != null) {
      return sContext
          .getSharedPreferences(name, Context.MODE_PRIVATE)
          .edit()
          .putString(key, value)
          .commit();
    }
    return false;
  }

  /**
   * Read the object from Base64 string.
   */
  private static Object fromString(String s) throws IOException,
      ClassNotFoundException {
    byte[] data = Base64.decode(s, Base64.DEFAULT);
    ObjectInputStream ois = new ObjectInputStream(
        new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return o;
  }

  /**
   * Write the object to a Base64 string.
   */
  private static String toString(Serializable o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(o);
    oos.close();
    return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
  }
}