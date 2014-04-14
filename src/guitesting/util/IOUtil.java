/*******************************************************************************
 * Copyright (c) 2010-2011, Gigon Bae
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  
 *     1. Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *     
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *     3. Neither the name of this project nor the names of its contributors may be
 *        used to endorse or promote products derived from this software without
 *        specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package guitesting.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOUtil {
  public static final Map<String, String> typeAliasMap;

  static {
    typeAliasMap = new HashMap<String, String>();
    typeAliasMap.put("void", "V");
    typeAliasMap.put("int", "I");
    typeAliasMap.put("long", "J");
    typeAliasMap.put("float", "F");
    typeAliasMap.put("double", "D");
    typeAliasMap.put("byte", "B");
    typeAliasMap.put("char", "C");
    typeAliasMap.put("short", "S");
    typeAliasMap.put("boolean", "Z");
  }
  public static Random RandomObj = new Random();

  public static final byte[] intToByteArray(int value) {
    return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
  }

  public static final int byteArrayToInt(byte[] b) {
    return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
  }

  public static Object loadObject(String dataFile) {
    return loadObject(dataFile, null);
  }

  public static Object loadObject(String dataFile, ClassLoader classLoader) {
    Object result = null;
    InputStream is = null;
    try {
      is = new FileInputStream(dataFile); // is = new BufferedInputStream(new FileInputStream(dataFile),
                                          // 16384);
      result = getObject(is, classLoader);
      is.close();
      is = null;
    } catch (Throwable e) {
      e.printStackTrace();
      result = null;
    } finally {
      if (is != null)
        try {
          is.close();
        } catch (Throwable e) {
          e.printStackTrace();
        }
    }
    return result;
  }

  public static Object getObject(InputStream dataFile) throws IOException {
    return getObject(dataFile, null);
  }

  public static Object getObject(InputStream dataFile, ClassLoader classLoader) throws IOException {
    MyObjectInputStream objects = null;
    try {
      objects = new MyObjectInputStream(dataFile, classLoader);
      Object data = objects.readObject();
      objects.close();
      objects = null;
      return data;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (objects != null) {
        try {
          objects.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void saveObject(Object data, OutputStream dataFile) {
    ObjectOutputStream objects = null;
    try {
      objects = new ObjectOutputStream(dataFile);
      objects.writeObject(data);
      objects.close();
      objects = null;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (objects != null) {
        try {
          objects.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void saveObject(Object data, String filename) {
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(filename);
      saveObject(data, fout);
      fout.close();
      fout = null;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fout != null)
        try {
          fout.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  public static Object loadObjectFromXML(String fileName) {
    Object result = null;
    FileInputStream fis;
    try {
      fis = new FileInputStream(fileName);
      result = TestProperty.xStream.fromXML(fis);
      fis.close();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return result;
  }

  public static Object deepCloneObj(Object obj) {
    String result = TestProperty.xStream.toXML(obj);
    return TestProperty.xStream.fromXML(result);
  }

  public static Object loadObjectFromZippedXML(String fileName) {
    Object result = null;
    ZipInputStream fis;
    try {
      fis = new ZipInputStream(new FileInputStream(fileName));
      fis.getNextEntry();
      result = TestProperty.xStream.fromXML(fis);
      fis.closeEntry();
      fis.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return result;
  }

  public static Object loadObjectFromXMLStream(InputStream stream) {
    Object result = null;
    try {
      result = TestProperty.xStream.fromXML(stream);
      stream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static Object loadObjectFromXMLURL(URL url) {
    Object result = null;
    InputStream stream = null;
    try {
      stream = url.openStream();
      result = TestProperty.xStream.fromXML(stream);
      stream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static boolean saveObjectToXML(Object obj, String fileName) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(fileName);
      TestProperty.xStream.toXML(obj, fos);
    } catch (Throwable e) {
      e.printStackTrace();
      return false;
    } finally {
      if (fos != null)
        try {
          fos.close();
        } catch (Throwable e) {
          e.printStackTrace();
        }
    }
    return true;
  }

  public static boolean saveObjectToZippedXML(Object obj, String fileName) {
    FileOutputStream fos = null;
    ZipOutputStream zos = null;
    try {
      fos = new FileOutputStream(fileName);
      zos = new ZipOutputStream(fos);
      zos.putNextEntry(new ZipEntry("content.xml"));
      TestProperty.xStream.toXML(obj, zos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (zos != null) {
          zos.closeEntry();
          zos.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return true;
  }

  public static void serializeObject(Object object, String filename) throws IOException {
    FileOutputStream fos = new FileOutputStream(filename);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(object);
    oos.close();
  }

  public static Object deSerializeObject(String filename) {
    return deSerializeObject(filename, null);
  }

  public static Object deSerializeObject(String filename, ClassLoader classLoader) {
    Object result = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      MyObjectInputStream ois = new MyObjectInputStream(fis, classLoader);
      Object obj = ois.readObject();
      result = obj;
      ois.close();
    } catch (Exception e) {
    }
    return result;
  }

  public static ByteArrayOutputStream serializeObject(Object object) {

    ByteArrayOutputStream objOutData = new ByteArrayOutputStream();
    try {
      // Serialize
      objOutData = new ByteArrayOutputStream();
      ObjectOutputStream oldOutAnalyzer = new ObjectOutputStream(objOutData);
      oldOutAnalyzer.writeObject(object);
      oldOutAnalyzer.reset();
      oldOutAnalyzer.close();
      return objOutData;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Object deSerializeObject(ByteArrayOutputStream objOutData) {
    return deSerializeObject(objOutData, null);
  }

  public static Object deSerializeObject(ByteArrayOutputStream objOutData, ClassLoader classLoader) {

    Object resultObject = null;
    ByteArrayInputStream objInpData = null;
    try {
      objInpData = new ByteArrayInputStream(objOutData.toByteArray());
      // Restore object
      MyObjectInputStream oldInpAnalyzer = new MyObjectInputStream(objInpData, classLoader);
      resultObject = oldInpAnalyzer.readObject();
      oldInpAnalyzer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultObject;
  }

  public static Object cloneObject(Object object) {
    return cloneObject(object, null);
  }

  public static Object cloneObject(Object object, ClassLoader classLoader) {

    Object resultObject = null;
    ByteArrayOutputStream objOutData = new ByteArrayOutputStream();
    ByteArrayInputStream objInpData = null;
    try {
      // Serialize
      objOutData = new ByteArrayOutputStream();
      ObjectOutputStream oldOutAnalyzer = new ObjectOutputStream(objOutData);
      oldOutAnalyzer.writeObject(object);
      oldOutAnalyzer.close();
      objInpData = new ByteArrayInputStream(objOutData.toByteArray());
      // Restore object
      MyObjectInputStream oldInpAnalyzer = new MyObjectInputStream(objInpData, classLoader);
      resultObject = oldInpAnalyzer.readObject();
      oldInpAnalyzer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultObject;
  }

  public static void copyFile(String srcFileName, String targetFileName) throws IOException {
    if (srcFileName.equals(targetFileName))
      return;

    FileChannel inChannel = null, outChannel = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      File src = new File(srcFileName);
      File target = new File(targetFileName);
      fileInputStream = new FileInputStream(src);
      inChannel = fileInputStream.getChannel();
      fileOutputStream = new FileOutputStream(target);
      outChannel = fileOutputStream.getChannel();
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      if (inChannel != null)
        inChannel.close();
      if (outChannel != null)
        outChannel.close();
      if (fileOutputStream != null)
        fileOutputStream.close();
      if (fileInputStream != null)
        fileInputStream.close();
    }
  }

  public static boolean deleteFile(String coveragefilename) {
    File file = new File(coveragefilename);
    if (file.exists()) {
      file.delete();
      return true;
    }
    return false;
  }

  public static boolean deleteFolder(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteFolder(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  public static void copyFolder(File srcPath, File dstPath) throws IOException {

    if (srcPath.isDirectory()) {
      if (!dstPath.exists()) {
        dstPath.mkdir();
      }

      String files[] = srcPath.list();
      for (int i = 0; i < files.length; i++) {
        if (srcPath.getName().toLowerCase().indexOf(".svn") == -1)
          copyFolder(new File(srcPath, files[i]), new File(dstPath, files[i]));
      }
    } else {
      if (!srcPath.exists()) {
        System.err.println("File or folder does not exist.(" + srcPath.getAbsolutePath() + ")");
        System.exit(1);
      } else {
        copyFile(srcPath.getAbsolutePath(), dstPath.getAbsolutePath());
      }
    }
  }

  public static boolean createNecessaryDirectories(String outputFolder, String name) {
    String folder = outputFolder + TestProperty.FileSeperator + "sootOutput";
    int index = name.lastIndexOf('.');
    if (index >= 0) {
      folder = folder + TestProperty.FileSeperator + name.substring(0, index);
      folder = folder.replaceAll("\\.", "/");
    }
    File file = new File(folder);
    if (!file.exists())
      return file.mkdirs();
    return true;
  }

  public static String getPathSeperatedString(Collection<String> classPathSet) {
    String result = "";
    for (String path : classPathSet) {
      result += TestProperty.PathSeperator + path;
    }
    return result.substring(1, result.length());
  }

  public static Object getContent(URL caseURL) {
    Object result = null;
    try {
      result = caseURL.getContent();
    } catch (IOException e) {
    }
    return result;
  }

  public static void executeCommand(List<String> command, String workspaceFolder) {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workspaceFolder)); // workspace folder is the base folder
    try {
      Process process;
      process = builder.start();

      InputStream is = process.getInputStream();
      InputStream es = process.getErrorStream();
      InputStreamReader isr = new InputStreamReader(is);
      InputStreamReader esr = new InputStreamReader(es);
      final BufferedReader br = new BufferedReader(isr);
      final BufferedReader ebr = new BufferedReader(esr);

      new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = br.readLine()) != null) {
              System.out.println(line);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).start();
      new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = ebr.readLine()) != null) {
              System.err.println(line);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).start();

      int exitVal = process.waitFor();
      if (exitVal != 0) {
        System.err.println("Exit value: " + exitVal);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void executeCommand(List<String> command, String workspaceFolder, int timeout) throws TimeoutException {
    if (timeout == 0) {
      executeCommand(command, workspaceFolder);
      return;
    }

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workspaceFolder)); // workspace folder is the base folder
    try {
      Process process;
      process = builder.start();

      InputStream is = process.getInputStream();
      InputStream es = process.getErrorStream();
      InputStreamReader isr = new InputStreamReader(is);
      InputStreamReader esr = new InputStreamReader(es);
      final BufferedReader br = new BufferedReader(isr);
      final BufferedReader ebr = new BufferedReader(esr);

      Thread stdThread = new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = br.readLine()) != null) {
              System.out.println(line);
            }
          } catch (Exception e) {
          }
        }
      });
      stdThread.start();
      Thread errThread = new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = ebr.readLine()) != null) {
              System.err.println(line);
            }
          } catch (Exception e) {
          }
        }
      });
      errThread.start();

      Worker worker = new Worker(process);
      worker.start();
      try {
        worker.join(timeout);
        if (worker.exit != null) {
          if (worker.exit != 0) {
            System.err.println("Exit value: " + worker.exit);
          }
        } else
          throw new TimeoutException();
      } catch (InterruptedException ex) {

        worker.interrupt();
        Thread.currentThread().interrupt();
        throw ex;
      } finally {
        stdThread.interrupt();
        errThread.interrupt();
        process.destroy();
      }
    } catch (TimeoutException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
    }

    // http://stackoverflow.com/questions/808276/how-to-add-a-timeout-value-when-using-javas-runtime-exec

  }

  private static class Worker extends Thread {
    private final Process process;
    private Integer exit;

    private Worker(Process process) {
      this.process = process;
    }

    public void run() {
      try {
        exit = process.waitFor();
      } catch (InterruptedException ignore) {
        return;
      }
    }
  }

  public static String getCommandExecution(List<String> command, String workspaceFolder) {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workspaceFolder)); // workspace folder is the base folder
    try {
      Process process;
      process = builder.start();

      InputStream is = process.getInputStream();
      InputStream es = process.getErrorStream();
      InputStreamReader isr = new InputStreamReader(is);
      InputStreamReader esr = new InputStreamReader(es);
      final BufferedReader br = new BufferedReader(isr);
      final BufferedReader ebr = new BufferedReader(esr);
      final StringBuffer result = new StringBuffer();
      final StringBuffer errResult = new StringBuffer();

      Thread outThread = new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = br.readLine()) != null) {
              result.append(line + "\n");
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

        }
      });
      outThread.start();
      Thread errThread = new Thread(new Runnable() {

        @Override
        public void run() {
          String line;
          try {
            while ((line = ebr.readLine()) != null) {
              errResult.append(line + "\n");
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
      errThread.start();

      outThread.join();
      errThread.join();

      int exitVal = process.waitFor();
      if (exitVal != 0) {
        System.out.println(result);
        System.err.println("Execution Error!");
        System.err.println(errResult);
        return "";
      }

      return result.toString();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

  public static String getSHA_256(byte[] inp, int maxLen) {

    // http://www.javamex.com/tutorials/cryptography/hash_functions_algorithms.shtml
    // http://forums.xkcd.com/viewtopic.php?f=11&t=16666&p=553936

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    md.update(inp);
    if (inp.length < maxLen) {
      md.update(new byte[maxLen - inp.length]);
    }
    byte[] mdbytes = md.digest();

    char[] chars = new char[2 * mdbytes.length];
    for (int i = 0; i < mdbytes.length; ++i) {
      chars[2 * i] = HEX_CHARS[(mdbytes[i] & 0xF0) >>> 4];
      chars[2 * i + 1] = HEX_CHARS[mdbytes[i] & 0x0F];
    }
    return new String(chars);
  }

  public static int countFiles(String folderPath) {
    File srcPath = new File(folderPath);
    int count = 0;
    if (srcPath.isDirectory()) {
      String files[] = srcPath.list();
      for (int i = 0; i < files.length; i++) {
        count += countFiles(new File(srcPath, files[i]).getAbsolutePath());
      }
    } else if (srcPath.exists()) {
      count = 1;
    }
    return count;
  }

  public static double sigmoid(double x) {
    return x / Math.sqrt(1 + x * x);
  }

  public static int weightedSelection(ArrayList<Double> accWeightValue, double weightSum) {
    int arraySize = accWeightValue.size();

    double probabilistic = getRandomDouble() * weightSum;

    int selectedIndex = findClosestWeight(accWeightValue, 0, arraySize - 1, probabilistic);
    return selectedIndex;
  }

  public static int findClosestWeight(ArrayList<Double> accWeightValue, int left, int right, double probabilistic) {
    if (left >= right) {
      return left;
    }

    int midIndex = (left + right) / 2;
    double midValue = accWeightValue.get(midIndex);

    if (probabilistic < midValue) {
      return findClosestWeight(accWeightValue, left, midIndex, probabilistic);
    } else { // midValue<=probabilistic
      return findClosestWeight(accWeightValue, midIndex + 1, right, probabilistic);
    }

  }

  public static double getRandomDouble() {
    return RandomObj.nextDouble();
  }

  public static Object invokeGetMethod(Object obj, String methodName) {
    Object result = null;
    try {

      Method method = obj.getClass().getMethod(methodName, new Class[0]);
      if (method != null) {
        result = method.invoke(obj, new Object[0]);
      }
    } catch (Exception e) {
    }

    return result;
  }

  public static Object invokeStaticGetMethod(Class<?> classObj, String methodName) {
    Object result = null;
    try {

      Method method = classObj.getMethod(methodName, new Class[0]);
      if (method != null) {
        result = method.invoke(null, new Object[0]);
      }
    } catch (Exception e) {
    }

    return result;
  }

  public static List<Class<?>> getClassesFromStr(ClassLoader cl, boolean initialize, String classString) {
    List<Class<?>> result = new ArrayList<Class<?>>();
    if (classString == null || classString.equals(""))
      return result;

    String[] classNameList = classString.split(";");
    for (String className : classNameList) {
      try {
        Class<?> handlerClass = Class.forName(className, initialize, cl);
        result.add(handlerClass);

      } catch (Throwable t) {
        t.printStackTrace();
      }

    }
    return result;
  }

  public static List<Object> getInstancesFromClassStr(ClassLoader cl, String classNames, Object... args) {
    List<Object> result = new ArrayList<Object>();
    List<Class<?>> classes = getClassesFromStr(cl, true, classNames);

    Class<?>[] argClasses = new Class<?>[args.length / 2];
    Object[] argObjs = new Object[args.length / 2];
    for (int i = 0; i < args.length / 2; i++) {
      argClasses[i] = (Class<?>) args[i * 2];
      argObjs[i] = (Object) args[i * 2 + 1];
    }

    for (Class<?> classItem : classes) {
      try {
        Constructor<?> constructor = classItem.getConstructor(argClasses);
        Object obj = constructor.newInstance(argObjs);
        result.add(obj);

      } catch (Throwable t) {
        t.printStackTrace();
      }

    }

    return result;
  }

  public static String getSimpleClassName(String className) {
    int pivot = className.lastIndexOf(".");
    String result = className.substring(pivot + 1);
    pivot = result.indexOf("$");
    return result = (pivot == -1) ? result : result.substring(0, pivot);
  }

  public static String getSimpleClassName2(String className) {
    int pivot = className.lastIndexOf(".");
    String result = className.substring(pivot + 1);
    return result;
  }

  public static void createInitialResultStructure() {
    TestProperty.getFolder("result").mkdirs();
    TestProperty.getFolder("error").mkdirs();
    TestProperty.getFolder("coverage").mkdirs();
    TestProperty.getFolder("metadata").mkdirs();
    TestProperty.getFolder("trace").mkdirs();
    TestProperty.getFolder("img.window").mkdirs();
    TestProperty.getFolder("img.component").mkdirs();
    TestProperty.getFolder("report").mkdirs();
  }

  public static void configureWorkspaceFiles() {
    IOUtil.deleteFolder(TestProperty.getFolder("input")); // delete input_files folder
    IOUtil.deleteFolder(TestProperty.getFolder("output")); // delete output_files folder

    IOUtil.createInitialResultStructure();

    File workspaceFile = TestProperty.getFolder("workspace");
    String[] fileList = workspaceFile.list();
    for (String file : fileList) {
      // delete if hidden files exist
      if (file.startsWith(".") && file.length() > 1) {
        File deleteFile = new File(workspaceFile, file);
        IOUtil.deleteFolder(deleteFile);
      }
    }

    try {
      File configFolder = TestProperty.getFolder("config");
      if (configFolder.exists())
        IOUtil.copyFolder(TestProperty.getFolder("config"), workspaceFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<String> getJVMArguments() {
    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
    List<String> aList = bean.getInputArguments();
    return aList;
  }

  /**
   * Get a collection of class names in a given folder 'file'
   * 
   * @param file
   * @return
   */
  public static List<String> getClassNameList(File file) {
    ArrayList<String> result = new ArrayList<String>();
    if (file == null || !file.isDirectory())
      return result;

    addClassNameList(file, "", result);
    return result;
  }

  private static void addClassNameList(File file, String classPath, List<String> collection) {

    if (file == null || !file.isDirectory())
      return;
    for (File f : file.listFiles()) {
      String name = f.getName();
      if (f.isDirectory()) {
        addClassNameList(new File(file, name), classPath + name + ".", collection);
      } else {
        if (name.endsWith(".class")) {
          String className = classPath + name.substring(0, name.length() - 6);
          collection.add(className);
        }
      }
    }
  }

  public static long getUsedMemory() {
    Runtime runtime = Runtime.getRuntime();
    long mb = 1024 * 1024;
    return (runtime.totalMemory() - runtime.freeMemory()) / mb;
  }

  public static String getMethodSignature(String className, Method m) {
    // String className = getFullClassName(m.getDeclaringClass());
    String methodName = m.getName();
    String typeStr = getMethodTypeSignature(m);
    StringBuffer buf = new StringBuffer();
    buf.append(className);
    buf.append(".");
    buf.append(methodName);
    buf.append(typeStr);
    return buf.toString();

  }

  private static String getMethodTypeSignature(Method m) {
    Class<?> result = m.getReturnType();
    Class<?>[] params = m.getParameterTypes();
    StringBuffer buf = new StringBuffer();
    buf.append("(");
    for (int i = 0; i < params.length; i++) {
      buf.append(makeTypeStr(params[i]));
    }
    buf.append(")");
    buf.append(makeTypeStr(result));
    return buf.toString();

  }

  public static String getFullClassName(Class<?> c) {
    Package pkg = c.getPackage();
    String packageName = (pkg == null) ? "" : pkg.getName();
    if (packageName == null || packageName.equals(""))
      return c.getName();
    else
      return packageName + "." + c.getName();

  }

  public static String makeTypeStr(Class<?> c) {
    String name = c.getName();
    String alias = typeAliasMap.get(name);
    if (alias != null) {
      return alias;
    } else {

      return makeTypeStr(name);
    }
  }

  public static String makeTypeStr(String name) {
    if (name.startsWith("[")) {
      return name.replace('.', '/');
    } else if (!name.endsWith(";")) {
      return "L" + name.replace('.', '/') + ";";
    } else {
      return name;
    }
  }

}
