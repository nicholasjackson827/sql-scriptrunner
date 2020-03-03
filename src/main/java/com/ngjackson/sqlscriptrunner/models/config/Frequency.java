package com.ngjackson.sqlscriptrunner.models.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Frequency {

  private long seconds;
  private long minutes;
  private long hours;

}
