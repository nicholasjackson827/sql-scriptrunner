package com.ngjackson.sqlscriptrunner.models.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Action {

  private String name;
  private ActionType type;
  private String folderBasePath;

  public enum ActionType {
    LOG
  }

}
