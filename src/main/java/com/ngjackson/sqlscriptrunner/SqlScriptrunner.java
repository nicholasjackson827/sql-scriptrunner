package com.ngjackson.sqlscriptrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ngjackson.sqlscriptrunner.models.config.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.beans.SimpleBeanInfo;
import java.io.File;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class SqlScriptrunner {

  public static void main(String[] args) throws Exception {

    System.out.println("Hello world!!!!!!!!!");

    Config config = parseYaml();

    System.out.println(ReflectionToStringBuilder.toString(config, ToStringStyle.MULTI_LINE_STYLE));


    try {
      // Grab the Scheduler instance from the Factory
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//      // define the job and tie it to our HelloJob class
//      JobDetail job = newJob(HelloJob.class)
//          .withIdentity("job1", "group1")
//          .build();
//
//
//      // Tell quartz to schedule the job using our trigger
//      scheduler.scheduleJob(job, trigger);

      // For each query in the config
      for (Query query : config.getQueries()) {

        // Create the job map with the data that the job needs to run
        Datasource datasource = config
            .getDatasources()
            .stream()
            .filter(ds -> ds.getName().equalsIgnoreCase(query.getSource()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unable to find datasource with name " + query.getSource() + " for query " + query.getName()));

        Action action = config
            .getActions()
            .stream()
            .filter(a -> a.getName().equalsIgnoreCase(query.getAction()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unable to find action with name " + query.getAction() + " for query " + query.getName()));

        JobDataMap dataMap = new JobDataMap();
        dataMap.put("datasource", datasource);
        dataMap.put("action", action);
        dataMap.put("query", query);

        // Create new job
        JobDetail job = newJob(SqlJob.class)
            .setJobData(dataMap)
            .withIdentity(query.getName())
            .build();

        // Create trigger for the job
        Trigger trigger = newTrigger()
            .withIdentity(query.getName())
            .startNow()
            .withSchedule(getScheduleForFrequency(query.getFrequency()))
            .build();

        // Add the trigger to the job
        scheduler.scheduleJob(job, trigger);

      }

      // and start it off
      scheduler.start();

      Thread.sleep(60000);

      scheduler.shutdown();

    } catch (SchedulerException | InterruptedException se) {
      se.printStackTrace();
    }

  }

  private static SimpleScheduleBuilder getScheduleForFrequency(Frequency frequency) {

    if (frequency.getSeconds() != 0) {
      return SimpleScheduleBuilder.repeatSecondlyForever((int) frequency.getSeconds());
    } else if (frequency.getMinutes() != 0) {
      return SimpleScheduleBuilder.repeatMinutelyForever((int) frequency.getMinutes());
    } else {
      return SimpleScheduleBuilder.repeatHourlyForever((int) frequency.getMinutes());
    }

  }

  private static Config parseYaml() throws Exception {

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(new File("src/main/resources/config.yml"), Config.class);

  }

}
