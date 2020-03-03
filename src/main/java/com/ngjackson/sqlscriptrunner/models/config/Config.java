package com.ngjackson.sqlscriptrunner.models.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Config {

  List<Datasource> datasources;
  List<Action> actions;
  List<Query> queries;

}
