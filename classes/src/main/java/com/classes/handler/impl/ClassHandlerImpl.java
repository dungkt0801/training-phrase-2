package com.classes.handler.impl;

import com.classes.entity.Class;
import com.classes.handler.ClassHandler;
import com.classes.service.ClassService;
import com.classes.util.ClassUtil;
import com.common.util.Util;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassHandlerImpl implements ClassHandler {

  private final ClassService classService;

  @Override
  public void findAll(RoutingContext rc) {
    classService.findAll(getQueryParams(rc))
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> Util.onErrorResponse(rc, 500, error)
      );
  }

  @Override
  public void findById(RoutingContext rc) {
    final String id = rc.pathParam("id");
    classService.findById(id)
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> Util.onErrorResponse(rc, 500, error),
        () -> Util.onErrorResponse(rc, 404, new NoSuchElementException("No class found with the id " + id))
      );
  }

  @Override
  public void insertOne(RoutingContext rc) {
    final Class clazz = ClassUtil.classFromJsonObject(rc.getBodyAsJson());
    classService.insertOne(clazz)
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> Util.onErrorResponse(rc, 500, error)
      );
  }

  @Override
  public void updateOne(RoutingContext rc) {
    final String id = rc.pathParam("id");
    final Class clazz = ClassUtil.classFromJsonObject(rc.getBodyAsJson());
    classService.updateOne(id, clazz)
      .subscribe(
        result -> Util.onSuccessResponse(rc, 200, result),
        error -> Util.onErrorResponse(rc, 500, error),
        () -> Util.onErrorResponse(rc, 404, new NoSuchElementException("No class found with the id " + id))
      );
  }

  @Override
  public void deleteOne(RoutingContext rc) {

  }

  @Override
  public void checkId(RoutingContext rc) {
    final String id = rc.pathParam("id");
    if(!Util.isValidObjectId(id)) {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException("Invalid student id"));
    } else {
      rc.next();
    }
  }

  @Override
  public void checkBody(RoutingContext rc) {
    JsonObject body = rc.getBodyAsJson();
    String validatedError = validateClassJsonObject(body);
    if(!validatedError.isEmpty()) {
      Util.onErrorResponse(rc, 400, new IllegalArgumentException(validatedError));
    } else {
      rc.next();
    }
  }

  private String validateClassJsonObject(JsonObject jsonObject) {

    if (jsonObject.isEmpty()) {
      return "Body is empty";
    }

    // Check if "name" field exists and is not empty
    if (!jsonObject.containsKey("className") || jsonObject.getString("className").isEmpty()) {
      return "Class name is required";
    }

    return "";
  }

  private JsonObject getQueryParams(RoutingContext rc) {
    MultiMap queryParams = rc.request().params();
    JsonObject query = new JsonObject();

    // class name
    String name = queryParams.get("className");
    if(name != null && !name.isEmpty()) {
      query.put("className", new JsonObject()
        .put("$regex", name.trim())
        .put("$options", "i")
      );
    }

    return query;
  }
}
