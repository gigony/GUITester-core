package guitesting.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import com.google.common.base.Joiner;

public class KeyValueStore extends Properties {
  private static final long serialVersionUID = 1L;

  public KeyValueStore() {
    super(null);
  }

  public KeyValueStore(String args) {
    super(null);
    parsingArgs(args);
  }

  public KeyValueStore(Properties defaultProperty, String args) {
    super(defaultProperty);
    parsingArgs(args);
  }

  public void setArgs(String args) {
    parsingArgs(args);
  }

  private void parsingArgs(String data) {
    for (String item : data.split(",")) {
      item = item.trim();
      if (item.equals(""))
        continue;
      int pivot = item.indexOf("=");
      if (pivot == -1)
        continue;
      String property = item.substring(0, pivot).toLowerCase();
      String value = item.substring(pivot + 1);
      put(property, value);
    }
  }

  public void setDefaultProperty(Properties property) {
    this.defaults = property;
  }

  public Properties getDefaultProperty() {
    return defaults;
  }

  public int getInt(String key) {
    return getInt(key, -1);
  }

  public int getInt(String key, int defaultValue) {
    key = key.toLowerCase();
    String value = getProperty(key);
    if (value != null)
      return Integer.parseInt(value);
    return defaultValue;
  }

  public String get(String key) {
    return get(key, "");
  }

  public String get(String key, String defaultValue) {
    key = key.toLowerCase();
    return getProperty(key, defaultValue);
  }

  public double getDouble(String key) {
    return getDouble(key, Double.NaN);
  }

  public double getDouble(String key, double defaultValue) {
    key = key.toLowerCase();
    String value = getProperty(key);
    if (value != null)
      return Double.parseDouble(value);
    return defaultValue;
  }

  public float getFloat(String key) {
    return getFloat(key, Float.NaN);
  }

  public float getFloat(String key, float defaultValue) {
    key = key.toLowerCase();
    String value = getProperty(key);
    if (value != null)
      return Float.parseFloat(value);
    return defaultValue;
  }

  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    key = key.toLowerCase();
    String value = getProperty(key);
    if (value != null)
      return Boolean.parseBoolean(value);
    return defaultValue;
  }

  public void set(String key, String value) {
    key = key.toLowerCase();
    setProperty(key, value);
  }

  public void setIfNull(String key, String value) {
    key = key.toLowerCase();
    if (getProperty(key) == null)
      setProperty(key, value);
  }

  public String getNsetIfNull(String key, String newVal) {
    key = key.toLowerCase();
    String val = getProperty(key);
    if (val == null) {
      setProperty(key, newVal);
      return newVal;
    } else {
      return val;
    }
  }

  public void saveProperties(File testPropertyFile) {
    try {
      // File testPropertyFile = TestProperty.getFile("guitester.default_properties");
      store(new FileOutputStream(testPropertyFile), "Test properties");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public synchronized String toString() {
    ArrayList<String> keyValuePairList = new ArrayList<String>(size());
    for (Object key : keySet()) {
      keyValuePairList.add(String.format("%s=%s", key, get(key)));
    }
    Collections.sort(keyValuePairList);
    return Joiner.on(",").join(keyValuePairList);
  }

}
