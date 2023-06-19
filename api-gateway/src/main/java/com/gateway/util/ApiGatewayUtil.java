package com.gateway.util;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import java.net.URI;
import java.util.Arrays;

public class ApiGatewayUtil {

  public static void onClientSuccessResponse(RoutingContext rc, int status, String result) {
    rc.response()
      .setStatusCode(status)
      .putHeader("Content-Type", "application/json")
      .end(result);
  }

  public static String getDestinationService(String path) {
    URI uri = URI.create(path);
    String keyword = uri.getPath().replaceFirst("^/([^/]+).*", "$1");
    return keyword;
  }

  public static void putHeadersForForwardingRequest(HttpRequest<Buffer> request, MultiMap headers) {
    headers.entries().forEach(header -> {
      if(!Arrays.asList("Host", "Content-Length", "Transfer-Encoding").contains(header.getKey())) {
        request.putHeader(header.getKey(), header.getValue());
      }
    });
  }

}
