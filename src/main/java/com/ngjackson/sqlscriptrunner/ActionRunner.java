package com.ngjackson.sqlscriptrunner;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.ngjackson.sqlscriptrunner.models.config.Action;
import com.ngjackson.sqlscriptrunner.models.config.Query;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ActionRunner {

  private static Logger logger = LoggerFactory.getLogger(ActionRunner.class);
  private static DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

  private static final int MAX_COLUMN_SIZE = 32;

  public static void doAction(Action action, Query query, List<HashMap<String, Object>> data) throws JobExecutionException {

    switch (action.getType()) {
      case LOG:
        doLog(data, query, action);
        break;
      default:
        throw new JobExecutionException("Unknown action of type " + action.getType());
    }

  }

  private static void doLog(List<HashMap<String, Object>> data, Query query, Action action) throws JobExecutionException {

    String logsFolder = action.getFolderBasePath() + "logs/";
    String logFileName = query.getName() + ".txt";

    List<String> logLines = new ArrayList<>();

    logLines.add("#######################");
    logLines.add("# NEW RESULTS ARE IN! #");
    logLines.add("#######################");
    logLines.add("Query: " + query.getName());
    logLines.add("\tNum results: " + data.size());
    logLines.add("\tSource:      " + query.getSource());
    logLines.add("\tAction:      " + query.getAction());
    logLines.add("\tRun time:    " + ZonedDateTime.now().format(formatter));
    logLines.add("\tLogged to:   " + logsFolder + logFileName);

    // First, log to the standard logger
    logLines.forEach(logger::info);

    // Then, try to log to the file
    try {
      // Create the log file (and path) if it doesn't exist
      Files.createDirectories(Paths.get(logsFolder));
      new File(logsFolder + logFileName).createNewFile();

      // Gather the data for the log
      String logLinesAsString = String.join("\n", logLines) + "\n";
      String headerRow = data.get(0).keySet().toString() + "\n";
      List<String> dataRows = new ArrayList<>();

      for (Map<String, Object> row : data) {
        dataRows.add(row.values().toString());
      }
      dataRows.add(""); // extra new line at end of each block

      // Write the log lines
      Files.write(
          Paths.get(logsFolder + logFileName),
          logLinesAsString.getBytes(),
          StandardOpenOption.APPEND
      );

      // Write the header rows
      Files.write(
          Paths.get(logsFolder + logFileName),
          headerRow.getBytes(),
          StandardOpenOption.APPEND
      );

      // Write the data rows
      Files.write(
          Paths.get(logsFolder + logFileName),
          dataRows,
          StandardOpenOption.APPEND
      );

    } catch (IOException e) {
      e.printStackTrace();
      throw new JobExecutionException(e);
    }

  }

}
