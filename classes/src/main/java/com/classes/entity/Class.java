package com.classes.entity;

import com.classes.util.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {

  private String id;

  private String className;

  private Long totalStudents;

  private Long enrolledStudents;

  private Long version;

  public Class(JsonObject jsonObject) {

    // id
    if (
      jsonObject.containsKey("_id") &&
        jsonObject.getValue("_id") instanceof JsonObject &&
        Util.isValidObjectId(jsonObject.getJsonObject("_id").getString("$oid"))
    ) {
      this.id = jsonObject.getJsonObject("_id").getString("$oid");
    }

    // className
    String className = jsonObject.getString("className");
    if(className != null && !className.isEmpty()) {
      this.className = className;
    }

    // totalStudents
    Long totalStudents = jsonObject.getLong("totalStudents");
    if(totalStudents != null && totalStudents >= 0) {
      this.totalStudents = totalStudents;
    } else {
      this.totalStudents = 0L;
    }

    // enrolledStudent
    Long enrolledStudents = jsonObject.getLong("enrolledStudents");
    if(enrolledStudents != null && enrolledStudents >= 0) {
      this.enrolledStudents = enrolledStudents;
    } else {
      this.enrolledStudents = 0L;
    }

    // version
    Long version = jsonObject.getLong("version");
    if(version != null && version >=0) {
      this.version = version;
    } else {
      this.version = 0L;
    }

  }

}
