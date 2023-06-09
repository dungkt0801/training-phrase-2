package com.common.util;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

public class Util {

  public static void onSuccessResponse(RoutingContext rc, int status, Object object) {
    rc.response()
      .setStatusCode(status)
      .putHeader("Content-Type", "application/json")
        .putHeader("Service-Port", String.valueOf(rc.request().localAddress().port()))
      .end(Json.encodePrettily(object));
  }

  public static void onErrorResponse(RoutingContext rc, int status, Throwable throwable) {
    JsonObject error = new JsonObject().put("error", throwable.getMessage());

    rc.response()
      .setStatusCode(status)
      .putHeader("Content-Type", "application/json")
        .putHeader("Service-Port", String.valueOf(rc.request().localAddress().port()))
      .end(Json.encodePrettily(error));
  }

  public static boolean isValidObjectId(String idString) {
    try {
      new ObjectId(idString);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

}
