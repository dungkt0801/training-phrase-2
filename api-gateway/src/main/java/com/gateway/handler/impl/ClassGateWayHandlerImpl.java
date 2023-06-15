package com.gateway.handler.impl;

import com.gateway.handler.ClassGatewayHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassGateWayHandlerImpl implements ClassGatewayHandler {

  private final WebClient webClient;

  public void handleClasses(RoutingContext routingContext) {
    // Code to handle GET request for classes
  }

  public void handleClassesPost(RoutingContext routingContext) {
    // Code to handle POST request for classes
  }

  public void handleClassesPut(RoutingContext routingContext) {
    // Code to handle PUT request for classes
  }

  public void handleClassesDelete(RoutingContext routingContext) {
    // Code to handle DELETE request for classes
  }

}
