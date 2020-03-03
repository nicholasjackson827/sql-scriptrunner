package com.ngjackson.sqlscriptrunner.models.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Query {

  private String name;
  private String query;
  private String action;
  private Frequency frequency;
  private String source;

}
