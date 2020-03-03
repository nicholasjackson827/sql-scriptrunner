package com.ngjackson.sqlscriptrunner;

import com.ngjackson.sqlscriptrunner.models.config.Action;
import com.ngjackson.sqlscriptrunner.models.config.Datasource;
import com.ngjackson.sqlscriptrunner.models.config.Query;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SqlJob implements Job {

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//    dataMap.put("datasource", datasource);
//    dataMap.put("action", action);
//    dataMap.put("query", query);

    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

    Datasource datasource = (Datasource) dataMap.get("datasource");
    Action action = (Action) dataMap.get("action");
    Query query = (Query) dataMap.get("query");

    // TODO: Convert this to using a connection pool, specifically HikariDataSource
    HikariConfig dsConfig = new HikariConfig();
    dsConfig.setJdbcUrl(datasource.getUrl());
    dsConfig.setUsername(datasource.getUsername());
    dsConfig.setPassword(datasource.getPassword());

    HikariDataSource ds = new HikariDataSource(dsConfig);

    List<HashMap<String, Object>> data = new ArrayList<>();

    try {
      Connection conn = ds.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query.getQuery());

      // Convert the result set to a more usable format for later
      ResultSetMetaData metaData = rs.getMetaData();
      while (rs.next()) {
        HashMap<String, Object> row = new HashMap<>();
        for (int i = 1; i <= metaData.getColumnCount(); ++i) {
          row.put(metaData.getColumnName(i), rs.getObject(i));
        }
        data.add(row);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new JobExecutionException(e);
    }

    ActionRunner.doAction(action, query, data);

  }

}
