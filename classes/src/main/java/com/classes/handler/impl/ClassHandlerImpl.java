package com.classes.handler.impl;

import com.classes.handler.ClassHandler;
import com.classes.service.ClassService;
import com.classes.util.Util;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;
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
    if(Util.isValidObjectId(id)) {
      classService.findById(id)
        .subscribe(
          result -> Util.onSuccessResponse(rc, 200, result),
          error -> Util.onErrorResponse(rc, 500, error),
          () -> Util.onErrorResponse(rc, 404, new NoSuchElementException("No class found with the id " + id)) // Called on onComplete
        );
    } else {
      Util.onErrorResponse(rc, 400, new NoSuchElementException("Invalid class id"));
    }
  }

  @Override
  public void insertOne(RoutingContext rc) {

  }

  @Override
  public void updateOne(RoutingContext rc) {

  }

  @Override
  public void deleteOne(RoutingContext rc) {

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
