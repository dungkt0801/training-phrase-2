package com.gateway.util;

import io.vertx.ext.web.RoutingContext;
import java.net.URI;

public class ApiGatewayUtil {

  public static void onClientSuccessResponse(RoutingContext rc, int status, String result) {
    rc.response()
      .setStatusCode(status)
      .putHeader("Content-Type", "application/json")
      .end(result);
  }

  public static String extractKeywordFromPath(String path) {
    URI uri = URI.create(path);
    String keyword = uri.getPath().replaceFirst("^/([^/]+).*", "$1");
    return keyword;
  }

}
