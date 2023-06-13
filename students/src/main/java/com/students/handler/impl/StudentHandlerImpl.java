package com.students.handler.impl;

import com.students.entity.Student;
import com.students.service.StudentService;
import com.students.util.StudentUtil;
import com.students.util.Util;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import com.students.handler.StudentHandler;

@RequiredArgsConstructor
public class StudentHandlerImpl implements StudentHandler {

  private final StudentService studentService;

  @Override
  public void findAll(RoutingContext rc) {
    studentService.findAll(getQueryParams(rc))
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> Util.onErrorResponse(rc, 500, error)
      );
  }

  @Override
  public void findById(RoutingContext rc) {
    final String id = rc.pathParam("id");
    if(Util.isValidObjectId(id)) {
      studentService.findById(id)
        .subscribe(
          result -> Util.onSuccessResponse(rc, 200, result),
          error -> Util.onErrorResponse(rc, 500, error),
          () -> Util.onErrorResponse(rc, 404, new NoSuchElementException("No student was found with the id " + id))
        );
    } else {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException("Invalid student id"));
    }
  }

  @Override
  public void insertOne(RoutingContext rc) {

    JsonObject body = rc.getBodyAsJson();

    String validatedError = validateStudentJsonObject(body);
    if(!validatedError.isEmpty()) {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException(validatedError));
      return;
    }

    final Student student = StudentUtil.studentFromJsonObject(body);
    studentService.insertOne(student)
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> handleInsertErrorResponse(rc, error)
      );
  }

  @Override
  public void updateOne(RoutingContext rc) {

    final String id = rc.pathParam("id");
    if(!Util.isValidObjectId(id)) {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException("Invalid student id"));
      return;
    }

    JsonObject body = rc.getBodyAsJson();
    String validatedError = validateStudentJsonObject(body);
    if(!validatedError.isEmpty()) {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException(validatedError));
      return;
    }

    final Student student = StudentUtil.studentFromJsonObject(body);
    studentService.updateOne(id, student)
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> {
          if(error instanceof NoSuchElementException) {
            Util.onErrorResponse(rc, 404, error);
          } else {
            Util.onErrorResponse(rc, 500, error);
          }
        }
      );
  }

  @Override
  public void deleteOne(RoutingContext rc) {

  }

  private void handleInsertErrorResponse(RoutingContext rc, Throwable error) {
    {
      if(error instanceof IllegalArgumentException) {
        Util.onErrorResponse(rc, 400, error);
      }
      else if (error instanceof NoSuchElementException) {
        Util.onErrorResponse(rc, 404, error);
      } else {
        Util.onErrorResponse(rc, 500, error);
      }
    }
  }

  private JsonObject getQueryParams(RoutingContext rc) {
    MultiMap queryParams = rc.request().params();
    JsonObject query = new JsonObject();

    // name
    String name = queryParams.get("name");
    if(name != null && !name.isEmpty()) {
      query.put("name", new JsonObject()
        .put("$regex", name.trim())
        .put("$options", "i")
      );
    }

    // classId
    String classId = queryParams.get("classId");
    if(classId != null && !classId.isEmpty()) {
      query.put("classId", new JsonObject().put("$oid", new ObjectId(classId.trim()).toString()));
    }

    // className
    String className = queryParams.get("className");
    if(className != null && !className.isEmpty()) {
      query.put("className", className.trim());
    }

    // birthday
    String birthday = queryParams.get("birthday");
    if(birthday != null && !birthday.isEmpty()) {
      query.put("birthday", birthday.trim());
    }

    return query;
  }

  private String validateStudentJsonObject(JsonObject jsonObject) {

    if (jsonObject.isEmpty()) {
      return "Body is empty";
    }

    // Check if "name" field exists and is not empty
    if (!jsonObject.containsKey("name") || jsonObject.getString("name").isEmpty()) {
      return "Student name is required";
    }

    // Check if "birthDay" field exists and follows the format "yyyy-MM-dd"
    if (jsonObject.containsKey("birthDay") && !jsonObject.getString("birthDay").isEmpty()) {
      String invalidBirthFormat = "Birthday must be in the 'yyyy-MM-dd' format";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      try {
        LocalDate.parse(jsonObject.getString("birthDay"), formatter);
      } catch (DateTimeParseException e) {
        return invalidBirthFormat;
      }
    }

    if (!jsonObject.containsKey("classId") || jsonObject.getString("classId").isEmpty()) {
      return "Class is required";
    }

    if(!Util.isValidObjectId(jsonObject.getString("classId"))) {
      return "Invalid class id";
    }

    return "";
  }

}
