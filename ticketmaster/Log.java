package ticketmaster;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Log class is complement some java.util.logging.* methods by adding automatic customization to loggers files, write the
 * log information to all files that are of that level and above if they exist, and to move the loggers files into a folder
 * to by the end of the program execution to keep them on record.
 * 
 * @author Robert J Alvarez
 * @date October 4th, 2022
 */
public class Log {
  private static HashMap<Level,Logger> loggers = new HashMap<>();
  //All possible levels: ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE, WARNING

  private Log() {}

  //methods
  /**
   * We customize our logger to write to FILENAME getting at least the level options
   * given by level and up. We also set logger to not output anything to System.out.
   * 
   * @param logger
   * @param level
   * @param filename
   */
  private static void customLogger(Logger logger, Level level, String filename) {
    try {
      FileHandler fh = new FileHandler(filename);
      fh.setFormatter(new SimpleFormatter());
      fh.setLevel(level);
      logger.addHandler(fh);
      logger.setLevel(level);
      logger.setUseParentHandlers(false);  //Don't print to System.out
    } catch (SecurityException | IOException e) {
      System.err.println("Error setting configurations for logger file " + filename);
      System.err.println(e.toString());
      e.printStackTrace();
    }
  }

  /**
   * 
   * 
   * @param level
   * @param str
   */
  public static synchronized void logWrite(Level level, String str) {
    if (!loggers.containsKey(level)) {
      Logger logger = Logger.getLogger(level.toString() + "Logger");
      customLogger(logger, level, logger.getName());
      loggers.put(level, logger);
    }

    //Write information to all the loggers that support the level pass or higher
    for (Logger log : loggers.values()) {
      log.log(level, str);
    }
  }

  /**
   * 
   */
  public static void movLogs() {
    File logDir = new File("./Loggers");
    try {
      if (!logDir.exists()) {  //If directory doesn't exist, we make it
        logDir.mkdirs();
      } else {    //If directory exist, remove all its files
        for (File file : logDir.listFiles()) {
          Files.delete(Paths.get("./Loggers/" + file.getName()));
        }
      }

      //Move all the Loggers created from current directory to Loggers directory
      String filename;
      for (Level level : loggers.keySet()) {
        filename = level.toString() + "Logger";
        if (!new File(filename).renameTo(new File("./Loggers/" + filename))) {
          System.out.println("File: " + filename + " couldn't be move to Loggers.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
