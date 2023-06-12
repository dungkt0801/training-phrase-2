package com.students.handler.impl;

import com.students.service.StudentService;
import com.students.util.Util;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
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
          error -> Util.onErrorResponse(rc, 500, error.getCause()),
          () -> Util.onErrorResponse(rc, 404, new NoSuchElementException("No student was found with the id " + id))
        );
    } else {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException("Invalid student id"));
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

}
