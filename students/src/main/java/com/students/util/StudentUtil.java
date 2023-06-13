package com.students.util;

import com.students.entity.Student;
import io.vertx.core.json.JsonObject;
import org.bson.types.ObjectId;

public class StudentUtil {

  public static Student studentFromJsonObject (JsonObject jsonObject) {
    Student student = new Student();

    String name = jsonObject.getString("name");
    if(name != null && !name.isEmpty()) {
      student.setName(name);
    }

    String birthday= jsonObject.getString("birthday");
    if (birthday != null && !birthday.isEmpty()) {
      student.setBirthday(birthday);
    }

    student.setClassId(jsonObject.getString("classId"));

    return student;
  }

  public static JsonObject jsonObjectFromStudent(Student student) {

    JsonObject jsonObject = new JsonObject();

    String name = student.getName();
    if(name != null) {
      jsonObject.put("name", name);
    }

    String birthday = student.getBirthday();
    if(birthday != null) {
      jsonObject.put("birthday", birthday);
    }

    String classId = student.getClassId();
    if(classId != null) {
      jsonObject.put("classId", new JsonObject().put("$oid", new ObjectId(classId).toString()));
    }

    return jsonObject;
  }

}
