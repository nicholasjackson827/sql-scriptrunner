package com.ngjackson.sqlscriptrunner.models.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Datasource {

  private String name;
  private String url;
  private String username;
  private String password;
  private String source;

}
