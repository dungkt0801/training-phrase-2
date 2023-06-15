package com.gateway.util;

import io.vertx.ext.web.RoutingContext;

public class ApiGatewayUtil {

  public static void onClientSuccessResponse(RoutingContext rc, int status, String result) {
    rc.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(result);
  }

}
