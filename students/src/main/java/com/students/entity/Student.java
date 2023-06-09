package com.students.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

  private String id;

  private String name;

  private String birthDay;

  private String classId;

  public Student(JsonObject jsonObject) {

    JsonObject idString = jsonObject.getJsonObject("_id");
    this.id = idString.getString("$oid");

    String name = jsonObject.getString("name");
    if(name != null && !name.isEmpty()) {
      this.name = jsonObject.getString("name");
    }

    String birthDay = jsonObject.getString("birthDay");
    if(birthDay != null && !birthDay.isEmpty()) {
      this.birthDay = jsonObject.getString("birthDay");
    }

    JsonObject classId = jsonObject.getJsonObject("classId");
    if(classId != null) {
      this.classId = classId.getString("$oid");
    } else {
      this.classId = null;
    }
  }

}
