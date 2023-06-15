package com.gateway.handler.impl;

import static com.gateway.constants.Constants.CLASSES_BASE_PATH;
import static com.gateway.constants.Constants.CLASSES_SERVICE_ROOT;

import com.common.util.Util;
import com.gateway.handler.ClassGatewayHandler;
import com.gateway.util.ApiGatewayUtil;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassGateWayHandlerImpl implements ClassGatewayHandler {

  private final WebClient webClient;

  public void handleClasses(RoutingContext routingContext) {

    String query = routingContext.request().query();
    String url = CLASSES_SERVICE_ROOT + CLASSES_BASE_PATH + routingContext.normalisedPath();
    if(query != null) {
      url += "?" + query;
    }

    webClient
      .getAbs(url)
      .send(result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleClassesPost(RoutingContext routingContext) {
    webClient
      .postAbs(CLASSES_SERVICE_ROOT + CLASSES_BASE_PATH + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleClassesPut(RoutingContext routingContext) {
    webClient
      .putAbs(CLASSES_SERVICE_ROOT + CLASSES_BASE_PATH + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleClassesDelete(RoutingContext routingContext) {
  }

}
