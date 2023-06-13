package com.students.entity;

import com.common.util.Util;
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

  private String birthday;

  private String classId;

  public Student(JsonObject jsonObject) {

    // id
    if (
      jsonObject.containsKey("_id") &&
        jsonObject.getValue("_id") instanceof JsonObject &&
        Util.isValidObjectId(jsonObject.getJsonObject("_id").getString("$oid"))
    ) {
      this.id = jsonObject.getJsonObject("_id").getString("$oid");
    }

    // student name
    String name = jsonObject.getString("name");
    if(name != null && !name.isEmpty()) {
      this.name = jsonObject.getString("name");
    }

    // birthday
    String birthday = jsonObject.getString("birthday");
    if(birthday != null && !birthday.isEmpty()) {
      this.birthday = jsonObject.getString("birthday");
    }

    // classId
    JsonObject classId = jsonObject.getJsonObject("classId");
    if(classId != null) {
      this.classId = classId.getString("$oid");
    } else {
      this.classId = null;
    }
  }

}
