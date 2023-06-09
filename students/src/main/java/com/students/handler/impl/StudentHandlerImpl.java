package com.students.handler.impl;

import com.students.service.StudentService;
import com.students.util.Util;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;
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

    // birthDay
    String birthDay = queryParams.get("birthDay");
    if(birthDay != null && !birthDay.isEmpty()) {
      query.put("birthDay", birthDay.trim());
    }

    return query;
  }

}
