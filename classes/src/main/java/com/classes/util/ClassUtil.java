package com.classes.util;

import com.classes.dto.ClassDto;
import com.classes.entity.Class;
import io.vertx.core.json.JsonObject;

public class ClassUtil {

  public static Class classFromJsonObject (JsonObject jsonObject) {
    Class clazz = new Class();

    String className = jsonObject.getString("className");
    if(className != null && !className.isEmpty()) {
      clazz.setClassName(className);
    }

    Long totalStudents = jsonObject.getLong("totalStudents");
    if(totalStudents != null && totalStudents >= 0) {
      clazz.setTotalStudents(totalStudents);
    }

    Long enrolledStudents = jsonObject.getLong("enrolledStudents");
    if(enrolledStudents != null && enrolledStudents >= 0) {
      clazz.setEnrolledStudents(enrolledStudents);
    }

    return clazz;
  }

  public static JsonObject jsonObjectFromClass(Class clazz) {

    JsonObject jsonObject = new JsonObject();

    String className = clazz.getClassName();
    if(className != null && !className.isEmpty()) {
      jsonObject.put("className", className);
    }

    Long totalStudents = clazz.getTotalStudents();
    if(totalStudents != null && totalStudents >= 0) {
      jsonObject.put("totalStudents", totalStudents);
    }

    Long enrolledStudents = clazz.getEnrolledStudents();
    if(enrolledStudents != null && enrolledStudents >= 0) {
      jsonObject.put("enrolledStudents", enrolledStudents);
    }

    return jsonObject;
  }

  public static ClassDto classToClassDto(Class clazz) {
    return ClassDto.builder()
      .id(clazz.getId())
      .className(clazz.getClassName())
      .totalStudents(clazz.getTotalStudents())
      .enrolledStudents(clazz.getEnrolledStudents())
      .build();
  }

}
