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

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

public class TestLogger {
  public static Logger log = Logger.getLogger(TestLogger.class);

  public static StringBuilder traceCache = new StringBuilder();
  public static StringBuilder debugCache = new StringBuilder();
  public static StringBuilder infoCache = new StringBuilder();
  public static StringBuilder warnCache = new StringBuilder();
  public static StringBuilder errCache = new StringBuilder();
  public static StringBuilder fatalCache = new StringBuilder();

  public static void setFileLoggerPath(String logFileName) {
    Enumeration appenders = log.getAllAppenders();
    FileAppender fa = null;
    while (appenders.hasMoreElements()) {
      Appender currAppender = (Appender) appenders.nextElement();
      if (currAppender instanceof FileAppender) {
        fa = (FileAppender) currAppender;
      }
    }
    if (fa != null) {
      fa.setFile(logFileName);
      fa.activateOptions();
    } else {
      log.info("No File Appender found");
    }

  }

  public static void deleteFileAppender() {
    Enumeration appenders = log.getAllAppenders();
    FileAppender fa = null;
    while (appenders.hasMoreElements()) {
      Appender currAppender = (Appender) appenders.nextElement();
      if (currAppender instanceof FileAppender) {
        fa = (FileAppender) currAppender;
      }
    }
    if (fa != null) {
      log.removeAppender(fa);
    } else {
      log.info("No File Appender found");
    }

  }

  public static void info_(String format, Object... args) {
    infoCache.append(String.format(format, args));
  }

  public static void info(String format, Object... args) {
    infoCache.append(String.format(format, args));
    log.info(infoCache);
    if (infoCache.length() > 0)
      infoCache = new StringBuilder();
  }

  public static void debug_(String format, Object... args) {
    debugCache.append(String.format(format, args));
  }

  public static void debug(String format, Object... args) {
    debugCache.append(String.format(format, args));
    log.debug(debugCache.toString());
    if (debugCache.length() > 0)
      debugCache = new StringBuilder();
  }

  public static void error_(String format, Object... args) {
    errCache.append(String.format(format, args));
  }

  public static void error(String format, Object... args) {
    errCache.append(String.format(format, args));
    log.error(errCache.toString());
    if (errCache.length() > 0)
      errCache = new StringBuilder();
  }

  public static void trace_(String format, Object... args) {
    traceCache.append(String.format(format, args));
  }

  public static void trace(String format, Object... args) {
    traceCache.append(String.format(format, args));
    log.trace(traceCache.toString());
    if (traceCache.length() > 0)
      traceCache = new StringBuilder();
  }

  public static void warn_(String format, Object... args) {
    warnCache.append(String.format(format, args));
  }

  public static void warn(String format, Object... args) {
    warnCache.append(String.format(format, args));
    log.warn(warnCache.toString());
    if (warnCache.length() > 0)
      warnCache = new StringBuilder();
  }

  public static void fatal_(String format, Object... args) {
    fatalCache.append(String.format(format, args));
  }

  public static void fatal(String format, Object... args) {
    fatalCache.append(String.format(format, args));
    log.fatal(fatalCache.toString());
    if (fatalCache.length() > 0)
      fatalCache = new StringBuilder();
  }

}
