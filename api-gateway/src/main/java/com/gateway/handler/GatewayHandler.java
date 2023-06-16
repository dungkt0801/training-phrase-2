package com.gateway.handler;

import io.vertx.ext.web.RoutingContext;

public interface GatewayHandler {

  void handleGet(RoutingContext routingContext);

  void handlePost(RoutingContext routingContext);

  void handlePut(RoutingContext routingContext);

  void handleDelete(RoutingContext routingContext);

}
