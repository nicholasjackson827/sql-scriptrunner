package com.ngjackson.sqlscriptrunner;

import com.ngjackson.sqlscriptrunner.models.config.Action;
import com.ngjackson.sqlscriptrunner.models.config.Datasource;
import com.ngjackson.sqlscriptrunner.models.config.Query;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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

    System.out.println("\nThis is when I would run this query!\n" + query.getQuery());

  }

}
