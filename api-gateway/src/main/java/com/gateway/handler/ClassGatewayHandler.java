package com.gateway.handler;

import io.vertx.ext.web.RoutingContext;

public interface ClassGatewayHandler {

  void handleClasses(RoutingContext routingContext);

  void handleClassesPost(RoutingContext routingContext);

  void handleClassesPut(RoutingContext routingContext);

  void handleClassesDelete(RoutingContext routingContext);

}
